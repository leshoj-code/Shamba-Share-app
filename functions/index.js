const functions  = require("firebase-functions");
const { defineString } = require("firebase-functions/params");
const admin      = require("firebase-admin");
const axios      = require("axios");

admin.initializeApp();
const db = admin.database();

const CONSUMER_KEY    = defineString("MPESA_CONSUMER_KEY");
const CONSUMER_SECRET = defineString("MPESA_CONSUMER_SECRET");
const SHORTCODE       = defineString("MPESA_SHORTCODE");
const PASSKEY         = defineString("MPESA_PASSKEY");

// ── Helpers ──────────────────────────────────────────────────────────────────

function getTimestamp() {
    const now = new Date();
    const pad = n => String(n).padStart(2, "0");
    return `${now.getFullYear()}${pad(now.getMonth()+1)}${pad(now.getDate())}` +
           `${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`;
}

async function getOAuthToken() {
    const credentials = Buffer.from(
        `${CONSUMER_KEY.value()}:${CONSUMER_SECRET.value()}`
    ).toString("base64");

    const response = await axios.get(
        "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials",
        { headers: { Authorization: `Basic ${credentials}` } }
    );
    return response.data.access_token;
}

// ── initiateStkPush ──────────────────────────────────────────────────────────

exports.initiateStkPush = functions.https.onRequest(async (req, res) => {
    // Allow CORS from Android
    res.set("Access-Control-Allow-Origin", "*");
    if (req.method === "OPTIONS") {
        res.set("Access-Control-Allow-Methods", "POST");
        res.set("Access-Control-Allow-Headers", "Content-Type");
        res.status(204).send("");
        return;
    }

    const { phone, amount, equipmentId, renterUid } = req.body;

    if (!phone || !amount || !equipmentId || !renterUid) {
        res.status(400).json({ success: false, error: "Missing required fields" });
        return;
    }

    try {
        const token     = await getOAuthToken();
        const shortcode = SHORTCODE.value();
        const passkey   = PASSKEY.value();
        const timestamp = getTimestamp();
        const password  = Buffer.from(`${shortcode}${passkey}${timestamp}`)
                            .toString("base64");

        // Generate a payment ID first so we can reference it in the callback
        const paymentRef = db.ref("Payments").push();
        const paymentId  = paymentRef.key;

        const stkResponse = await axios.post(
            "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest",
            {
                BusinessShortCode: shortcode,
                Password:          password,
                Timestamp:         timestamp,
                TransactionType:   "CustomerPayBillOnline",
                Amount:            amount,
                PartyA:            phone,
                PartyB:            shortcode,
                PhoneNumber:       phone,
                CallBackURL:       `https://${process.env.GCLOUD_PROJECT}.cloudfunctions.net/mpesaCallback`,
                AccountReference:  `ShambaShare-${paymentId}`,
                TransactionDesc:   "Equipment Rental Payment"
            },
            { headers: { Authorization: `Bearer ${token}` } }
        );

        const checkoutId = stkResponse.data.CheckoutRequestID;

        // Save pending payment record
        await paymentRef.set({
            id:                paymentId,
            equipmentId:       equipmentId,
            renterUid:         renterUid,
            amount:            amount,
            phone:             phone,
            status:            "pending",
            checkoutRequestId: checkoutId,
            timestamp:         admin.database.ServerValue.TIMESTAMP,
            method:            "mpesa"
        });

        // Also store checkoutId → paymentId mapping for fast callback lookup
        await db.ref(`CheckoutIndex/${checkoutId}`).set({
            paymentId:   paymentId,
            equipmentId: equipmentId,
            renterUid:   renterUid,
            amount:      amount
        });

        res.status(200).json({ success: true, paymentId: paymentId });

    } catch (error) {
        console.error("STK Push error:", error.response?.data || error.message);
        res.status(500).json({
            success: false,
            error: error.response?.data?.errorMessage || error.message
        });
    }
});

// ── mpesaCallback ─────────────────────────────────────────────────────────────

exports.mpesaCallback = functions.https.onRequest(async (req, res) => {
    try {
        const body        = req.body.Body.stkCallback;
        const resultCode  = body.ResultCode;
        const checkoutId  = body.CheckoutRequestID;

        // Look up which payment this belongs to
        const indexSnap = await db.ref(`CheckoutIndex/${checkoutId}`).get();
        if (!indexSnap.exists()) {
            console.log("Unknown checkout ID:", checkoutId);
            res.status(200).json({ ResultCode: 0, ResultDesc: "Accepted" });
            return;
        }

        const { paymentId, equipmentId, renterUid, amount } = indexSnap.val();

        if (resultCode === 0) {
            // Payment successful
            const metadata  = body.CallbackMetadata.Item;
            const mpesaRef  = metadata.find(i => i.Name === "MpesaReceiptNumber")?.Value || "";
            const paidAmount = metadata.find(i => i.Name === "Amount")?.Value || amount;

            // Update payment record
            await db.ref(`Payments/${paymentId}`).update({
                status:    "completed",
                mpesaRef:  mpesaRef,
                amount:    paidAmount,
                completedAt: admin.database.ServerValue.TIMESTAMP
            });

            // Mark equipment as Idle again
            await db.ref(`Equipment/${equipmentId}`).update({
                status:        "Idle",
                currentRenter: null
            });

            // Notify renter
            await db.ref(`Notifications/${renterUid}`).push().set({
                title:   "Payment Confirmed",
                message: `Your payment of KES ${paidAmount} was confirmed. Ref: ${mpesaRef}`,
                type:    "payment",
                ref:     mpesaRef,
                isRead:  false,
                time:    admin.database.ServerValue.TIMESTAMP
            });

            // Notify owner — find owner from equipment
            const equipSnap = await db.ref(`Equipment/${equipmentId}/ownerUid`).get();
            if (equipSnap.exists()) {
                await db.ref(`Notifications/${equipSnap.val()}`).push().set({
                    title:   "Payment Received",
                    message: `KES ${paidAmount} received via M-Pesa. Ref: ${mpesaRef}`,
                    type:    "payment",
                    ref:     mpesaRef,
                    isRead:  false,
                    time:    admin.database.ServerValue.TIMESTAMP
                });
            }

        } else {
            // Payment failed or cancelled
            await db.ref(`Payments/${paymentId}`).update({
                status:      "failed",
                resultCode:  resultCode,
                completedAt: admin.database.ServerValue.TIMESTAMP
            });

            // Notify renter of failure
            await db.ref(`Notifications/${renterUid}`).push().set({
                title:   "Payment Failed",
                message: "Your M-Pesa payment was not completed. Please try again.",
                type:    "payment",
                isRead:  false,
                time:    admin.database.ServerValue.TIMESTAMP
            });
        }

        // Always respond 200 to Safaricom
        res.status(200).json({ ResultCode: 0, ResultDesc: "Accepted" });

    } catch (error) {
        console.error("Callback error:", error);
        res.status(200).json({ ResultCode: 0, ResultDesc: "Accepted" });
    }
});