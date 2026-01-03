import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

class DiseasesPage extends StatefulWidget {
  const DiseasesPage({super.key});

  @override
  State<DiseasesPage> createState() => _DiseasesPageState();
}

class _DiseasesPageState extends State<DiseasesPage> {
  final ImagePicker _picker = ImagePicker();
  File? _selectedImage;

  Map<String, dynamic>? aiResult;
  bool isLoading = false;
  String? token;

  static const Color primaryGreen = Color(0xFF5DB075);
  static const Color backgroundColor = Color(0xFFFDF7F9);

  static const String apiUrl =
      "http://localhost:8083/images/upload"; // Gateway ou localhost pour émulateur

  @override
  void initState() {
    super.initState();
    _loadToken();
  }

  Future<void> _loadToken() async {
    final prefs = await SharedPreferences.getInstance();
    final storedToken = prefs.getString('token');
    if (storedToken != null) {
      setState(() => token = storedToken);
    } else {
      // Rediriger vers login si pas de token
      Navigator.pushReplacementNamed(context, '/login');
    }
  }

  // ================= IMAGE PICKERS =================
  Future<void> _pickFromCamera() async {
    final XFile? image = await _picker.pickImage(source: ImageSource.camera);
    if (image != null) {
      setState(() {
        _selectedImage = File(image.path);
        aiResult = null;
      });
    }
  }

  Future<void> _pickFromGallery() async {
    final XFile? image = await _picker.pickImage(source: ImageSource.gallery);
    if (image != null) {
      setState(() {
        _selectedImage = File(image.path);
        aiResult = null;
      });
    }
  }

  // ================= IA CALL =================
  Future<void> _analyzeImage() async {
    if (_selectedImage == null || token == null) return;

    setState(() => isLoading = true);

    try {
      final request = http.MultipartRequest("POST", Uri.parse(apiUrl));

      request.files.add(
        await http.MultipartFile.fromPath("file", _selectedImage!.path),
      );

      // Ajouter token JWT dans l'en-tête
      request.headers.addAll({
        "Authorization": "Bearer $token",
      });

      final response = await request.send();
      final body = await response.stream.bytesToString();

      if (response.statusCode == 200) {
        final decoded = json.decode(body);
        setState(() {
          aiResult = decoded["prediction"];
        });
      } else if (response.statusCode == 401) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Erreur 401 : Non autorisé")),
        );
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Erreur ${response.statusCode}: $body")),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Erreur IA : $e")),
      );
    } finally {
      setState(() => isLoading = false);
    }
  }

  // ================= UI =================
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: backgroundColor,
      appBar: AppBar(
        backgroundColor: primaryGreen,
        elevation: 0,
        title: const Text(
          'Agri AI',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 22),
        ),
        centerTitle: true,
        leading: const Icon(Icons.menu),
        actions: const [
          Icon(Icons.search),
          SizedBox(width: 12),
          Icon(Icons.notifications_none),
          SizedBox(width: 12),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Plant Problems?',
              style: TextStyle(
                fontSize: 26,
                fontWeight: FontWeight.bold,
                color: primaryGreen,
              ),
            ),
            const SizedBox(height: 15),
            _uploadCard(),
            const SizedBox(height: 40),
            _spotlightBlock(),
          ],
        ),
      ),
    );
  }

  // ================= UPLOAD CARD =================
  Widget _uploadCard() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, 5),
          ),
        ],
      ),
      child: Column(
        children: [
          const Text(
            'Find the best solutions for plant health!',
            textAlign: TextAlign.center,
            style: TextStyle(fontSize: 16),
          ),
          const SizedBox(height: 25),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              _iconColumn(
                icon: Icons.eco,
                label: 'Identify\nProblem',
                color: primaryGreen,
              ),
              const SizedBox(width: 30),
              const Icon(Icons.arrow_forward),
              const SizedBox(width: 30),
              _iconColumn(
                icon: Icons.search,
                label: 'Get\nSolution',
                color: Colors.blue,
              ),
            ],
          ),
          const SizedBox(height: 30),
          SizedBox(
            width: double.infinity,
            height: 55,
            child: ElevatedButton.icon(
              onPressed: _pickFromCamera,
              icon: const Icon(Icons.camera_alt),
              label: const Text(
                'Take a Picture',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              style: ElevatedButton.styleFrom(
                backgroundColor: primaryGreen,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30),
                ),
              ),
            ),
          ),
          const SizedBox(height: 15),
          TextButton.icon(
            onPressed: _pickFromGallery,
            icon: const Icon(Icons.photo_library, color: primaryGreen),
            label: const Text(
              'Upload image from gallery',
              style: TextStyle(
                color: primaryGreen,
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
          const SizedBox(height: 20),
          if (_selectedImage != null)
            ClipRRect(
              borderRadius: BorderRadius.circular(15),
              child: Image.file(
                _selectedImage!,
                height: 200,
                width: double.infinity,
                fit: BoxFit.cover,
              ),
            ),
          if (_selectedImage != null) ...[
            const SizedBox(height: 20),
            SizedBox(
              width: double.infinity,
              height: 50,
              child: ElevatedButton(
                onPressed: isLoading ? null : _analyzeImage,
                style: ElevatedButton.styleFrom(
                  backgroundColor: primaryGreen,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(30),
                  ),
                ),
                child: isLoading
                    ? const CircularProgressIndicator(color: Colors.white)
                    : const Text(
                        "Analyze Image",
                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                      ),
              ),
            ),
          ],
        ],
      ),
    );
  }

  // ================= SPOTLIGHT BLOCK =================
  Widget _spotlightBlock() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: const [
            Text(
              'In the Spotlight',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            Text(
              'Read More →',
              style: TextStyle(fontSize: 14, color: primaryGreen),
            ),
          ],
        ),
        const SizedBox(height: 20),
        if (aiResult == null)
          const Center(
            child: Text(
              'No news highlights available right now.',
              style: TextStyle(color: Colors.grey),
            ),
          )
        else
          Container(
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(20),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withOpacity(0.05),
                  blurRadius: 10,
                ),
              ],
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  aiResult!["details"]["name"],
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: primaryGreen,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  "Confidence: ${aiResult!["confidence"]}%",
                  style: const TextStyle(fontWeight: FontWeight.w600),
                ),
                const SizedBox(height: 12),
                const Text(
                  "Recommended Treatment:",
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 8),
                ...List.generate(
                  aiResult!["details"]["treatment"].length,
                  (index) => Padding(
                    padding: const EdgeInsets.only(bottom: 6),
                    child: Row(
                      children: [
                        const Text("• "),
                        Expanded(
                          child: Text(
                            aiResult!["details"]["treatment"][index],
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
      ],
    );
  }

  Widget _iconColumn({
    required IconData icon,
    required String label,
    required Color color,
  }) {
    return Column(
      children: [
        CircleAvatar(
          radius: 30,
          backgroundColor: color.withOpacity(0.15),
          child: Icon(icon, size: 30, color: color),
        ),
        const SizedBox(height: 10),
        Text(label, textAlign: TextAlign.center),
      ],
    );
  }
}
