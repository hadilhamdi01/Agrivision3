# AI Service - Plant Disease Detection 🌱

Service d'intelligence artificielle pour la détection des maladies des plantes utilisant un CNN (Convolutional Neural Network).

## 📋 Prérequis

- Python 3.8 ou supérieur
- Dataset PlantVillage dans `C:/Users/Admin/PlantVillage`

## 🚀 Installation

1. Installer les dépendances :
```bash
pip install -r requirements.txt
```

## 🎓 Entraînement du modèle

**Étape 1 : Entraîner le CNN sur le dataset**
```bash
python train_model.py
```

Cela va :
- Charger le dataset depuis `C:/Users/Admin/PlantVillage`
- Construire un CNN avec plusieurs couches de convolution
- Entraîner le modèle (50 époques par défaut)
- Sauvegarder le modèle dans `models/plant_disease_cnn.h5`
- Sauvegarder les classes dans `models/classes.json`
- Générer les graphiques d'apprentissage

⏱️ **Durée estimée** : 30-60 minutes selon votre GPU/CPU

## 🌐 Lancement de l'API

**Étape 2 : Démarrer le service API**
```bash
python app.py
```

L'API sera accessible sur `http://localhost:8083`

## 📡 Endpoints disponibles

### 1. Page d'accueil
```
GET http://localhost:8083/
```

### 2. Vérification de santé
```
GET http://localhost:8083/health
```

### 3. Liste des classes
```
GET http://localhost:8083/classes
```

### 4. Prédiction (image unique)
```
POST http://localhost:8083/predict
Content-Type: multipart/form-data
Body: file=<image>
```

**Réponse exemple :**
```json
{
  "success": true,
  "prediction": {
    "plant": "Tomato",
    "disease": "Late blight",
    "confidence": 0.95,
    "class_full": "Tomato_Late_blight"
  },
  "top_3_predictions": [
    {"class": "Tomato_Late_blight", "confidence": 0.95},
    {"class": "Tomato_Early_blight", "confidence": 0.03},
    {"class": "Tomato_healthy", "confidence": 0.01}
  ],
  "timestamp": "2025-12-24T15:30:00"
}
```

### 5. Prédiction batch (plusieurs images)
```
POST http://localhost:8083/batch-predict
Content-Type: multipart/form-data
Body: files=[<image1>, <image2>, ...]
```

## 🏗️ Architecture du CNN

```
Input (128x128x3)
↓
Conv2D (32) → BatchNorm → MaxPool → Dropout
↓
Conv2D (64) → BatchNorm → MaxPool → Dropout
↓
Conv2D (128) → BatchNorm → MaxPool → Dropout
↓
Conv2D (256) → BatchNorm → MaxPool → Dropout
↓
Flatten
↓
Dense (512) → BatchNorm → Dropout
↓
Dense (256) → BatchNorm → Dropout
↓
Dense (num_classes) → Softmax
```

## 📊 Fichiers générés

Après l'entraînement :
- `models/plant_disease_cnn.h5` : Modèle entraîné
- `models/classes.json` : Liste des classes
- `models/best_model.h5` : Meilleur modèle (val_accuracy)
- `models/training_history.png` : Graphiques d'apprentissage

## 🔗 Intégration avec les autres services

### Depuis image-service (Java)
```java
// Upload image et obtenir prédiction
RestTemplate restTemplate = new RestTemplate();
MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
body.add("file", new FileSystemResource(imageFile));

ResponseEntity<PredictionResponse> response = restTemplate.postForEntity(
    "http://localhost:8083/predict",
    new HttpEntity<>(body),
    PredictionResponse.class
);
```

### Depuis Flutter
```dart
// Upload et prédiction
var request = http.MultipartRequest(
  'POST', 
  Uri.parse('http://localhost:8083/predict')
);
request.files.add(await http.MultipartFile.fromPath('file', imagePath));
var response = await request.send();
```

## 🐛 Dépannage

**Problème : "Modèle non chargé"**
- Solution : Exécutez d'abord `python train_model.py`

**Problème : "Dataset non trouvé"**
- Solution : Vérifiez que le dataset est dans `C:/Users/Admin/PlantVillage`

**Problème : Mémoire insuffisante**
- Solution : Réduisez `BATCH_SIZE` dans `train_model.py` (ex: 16 ou 8)

## 📈 Performance attendue

Avec le dataset complet :
- **Précision** : 85-95%
- **Temps de prédiction** : <1 seconde par image
- **Taille du modèle** : ~50 MB

## 🔧 Configuration

Modifiez les paramètres dans `train_model.py` :
```python
DATASET_PATH = "C:/Users/Admin/PlantVillage"
IMG_SIZE = (128, 128)  # Augmenter pour meilleure précision
BATCH_SIZE = 32
EPOCHS = 50
```

## 📝 Logs

Les logs sont affichés dans la console pendant :
- L'entraînement : progression par époque
- Les prédictions : requêtes reçues et résultats

## 🎯 Port utilisé

- **8083** : API AI Service

Assurez-vous que ce port est disponible.
