import numpy as np
from PIL import Image

IMG_SIZE = (128, 128)

def preprocess_image(image: Image.Image):
    """
    Reproduit exactement le prétraitement du Colab
    """
    image = image.convert("RGB")
    image = image.resize(IMG_SIZE)

    img_array = np.array(image) / 255.0
    img_array = np.expand_dims(img_array, axis=0)

    return img_array
