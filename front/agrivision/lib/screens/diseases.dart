import 'package:flutter/material.dart';

class DiseasesPage extends StatelessWidget {
  const DiseasesPage({super.key});

  @override
  Widget build(BuildContext context) {
    // Couleur principale verte (tu peux garder ta couleur ou utiliser celle-ci pour plus de cohérence)
    final Color primaryGreen = const Color(0xFF4CAF50); 
    // Ou garder ta couleur : const Color.fromARGB(255, 118, 173, 129)

    return Scaffold(
      appBar: AppBar(
        backgroundColor: primaryGreen,
        foregroundColor: Colors.white,
        elevation: 0,
        title: const Text(
          'Disease Scanner',
          style: TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.pop(context),
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Icône ou illustration grande (tu peux remplacer par une image personnalisée)
            Icon(
              Icons.camera_alt,
              size: 120,
              color: primaryGreen.withOpacity(0.8),
            ),
            // Ou utiliser une image SVG/asset :
            // Image.asset('assets/plant_scanner.png', width: 180),

            const SizedBox(height: 40),

            const Text(
              'Scan Your Plant',
              style: TextStyle(
                fontSize: 32,
                fontWeight: FontWeight.bold,
                color: Colors.black87,
              ),
              textAlign: TextAlign.center,
            ),

            const SizedBox(height: 20),

            Text(
              'Take a clear photo of the affected leaf or plant to identify diseases and get instant solutions.',
              style: TextStyle(
                fontSize: 18,
                color: Colors.grey[700],
                height: 1.5,
              ),
              textAlign: TextAlign.center,
            ),

            const SizedBox(height: 60),

            // Bouton principal identique à celui de la page Home
            ElevatedButton.icon(
              onPressed: () {
                // TODO: Implémenter l'ouverture de la caméra (avec image_picker par exemple)
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Opening camera... (to be implemented)')),
                );
              },
              icon: const Icon(Icons.camera_alt, size: 32),
              label: const Text(
                'Take a Picture',
                style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
              ),
              style: ElevatedButton.styleFrom(
                backgroundColor: primaryGreen,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(horizontal: 50, vertical: 20),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30),
                ),
                elevation: 8,
                shadowColor: primaryGreen.withOpacity(0.4),
              ),
            ),

            const SizedBox(height: 30),

            // Option secondaire : choisir depuis la galerie
            TextButton(
              onPressed: () {
                // TODO: Ouvrir la galerie
              },
              child: const Text(
                'Or choose from gallery',
                style: TextStyle(fontSize: 16, color: Colors.grey),
              ),
            ),
          ],
        ),
      ),
    );
  }
}