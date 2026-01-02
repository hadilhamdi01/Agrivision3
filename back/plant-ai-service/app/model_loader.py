from tensorflow.keras.models import load_model

MODEL_PATH = "models/best_cnn_model.h5"

model = None

def load_ai_model():
    global model
    if model is None:
        model = load_model(MODEL_PATH)
    return model
