const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

const db = admin.firestore();

// Roman Urdu se Urdu mein tabdeel karne wala function
function romanToUrdu(romanText) {
    const charMap = {
        "khh": "کھ", "kh": "خ", "gh": "غ", "sh": "ش", "ch": "چ", "bh": "بھ",
        "ph": "پھ", "th": "تھ", "dh": "دھ", "rh": "رھ", "a": "ا", "b": "ب",
        "p": "پ", "t": "ت", "j": "ج", "h": "ہ", "x": "خ", "d": "د", "r": "ر",
        "z": "ز", "s": "س", "f": "ف", "q": "ق", "k": "ک", "g": "گ", "l": "ل",
        "m": "م", "n": "ن", "v": "و", "w": "و", "y": "ی", "e": "ی", "i": "ی",
        "o": "و", "u": "و", " ": " ",
    };
    let text = romanText.toLowerCase();
    let urduText = "";
    let i = 0;
    while (i < text.length) {
        let matched = false;
        for (let j = 3; j > 0; j--) {
            if (i + j <= text.length) {
                const sub = text.substring(i, i + j);
                if (charMap[sub]) {
                    urduText += charMap[sub];
                    i += j;
                    matched = true;
                    break;
                }
            }
        }
        if (!matched) {
            urduText += text[i];
            i++;
        }
    }
    return urduText;
}


exports.addSearchKeywordsToProducts = functions.https.onRequest(async (req, res) => {
    try {
        const productsSnapshot = await db.collection("products").get();
        const batch = db.batch();
        let updatedCount = 0;

        productsSnapshot.forEach(doc => {
            const product = doc.data();
            // Check karein ke product mein naam hai aur keywords pehle se mojood nahi hain
            if (product.name && !product.searchKeywords) {
                const name = product.name.toLowerCase();
                const keywords = new Set();

                // Naam ko lafzon mein torein
                const nameParts = name.split(" ").filter((part) => part.length > 0);
                nameParts.forEach((part) => keywords.add(part));

                // Poore naam ko bhi shamil karein
                keywords.add(name);

                // Roman Urdu ko Urdu mein tabdeel karein
                const urduName = romanToUrdu(name);
                const urduNameParts = urduName.split(" ").filter((part) => part.length > 0);
                urduNameParts.forEach((part) => keywords.add(part));
                keywords.add(urduName);

                // Document ko update ke liye batch mein shamil karein
                batch.update(doc.ref, { searchKeywords: Array.from(keywords) });
                updatedCount++;
            }
        });

        // Batch ko execute karein
        await batch.commit();

        res.status(200).send(`Successfully updated ${updatedCount} products with search keywords.`);
    } catch (error) {
        console.error("Error updating products:", error);
        res.status(500).send("An error occurred while updating products.");
    }
});