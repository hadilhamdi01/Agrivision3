import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

class AiImageService {
  static const String baseUrl = "http://localhost:8083";

  static Future<Map<String, dynamic>> uploadImage(File image) async {
    final uri = Uri.parse("$baseUrl/images/upload");

    final request = http.MultipartRequest("POST", uri);
    request.files.add(
      await http.MultipartFile.fromPath("file", image.path),
    );

    final response = await request.send();
    final responseBody = await response.stream.bytesToString();

    if (response.statusCode == 200) {
      return json.decode(responseBody);
    } else {
      throw Exception("Erreur IA ${response.statusCode}");
    }
  }
}
