import io
import numpy as np
from fastapi import FastAPI, File, UploadFile, HTTPException
from PIL import Image
from datetime import datetime

from app.model_loader import load_ai_model
from app.image_utils import preprocess_image
from app.classes import CLASS_NAMES
from treatments import TREATMENTS

app = FastAPI(
    title="Plant Disease AI Service",
    description="Service IA de classification des maladies des plantes",
    version="1.0.0"
)

model = load_ai_model()

@app.get("/health")
def health_check():
    return {"status": "UP", "service": "AI-Service"}

@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    try:
        # Vérification type fichier
        if not file.content_type.startswith("image/"):
            raise HTTPException(status_code=400, detail="Le fichier doit être une image")

        # Lire l'image
        contents = await file.read()
        image = Image.open(io.BytesIO(contents))

        # Prétraitement
        input_data = preprocess_image(image)

        # Prédiction
        predictions = model.predict(input_data)
        predicted_index = int(np.argmax(predictions[0]))
        confidence = float(np.max(predictions[0]))

        return {
            "timestamp": datetime.utcnow(),
            "predicted_class": CLASS_NAMES[predicted_index],
            "confidence": round(confidence * 100, 2),
            "details": TREATMENTS.get(CLASS_NAMES[predicted_index], {})
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
