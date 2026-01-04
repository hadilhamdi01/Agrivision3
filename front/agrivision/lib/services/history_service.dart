import 'dart:convert';
import 'package:http/http.dart' as http;

class HistoryService {
  static const String baseUrl = "http://localhost:8083";

  static Future<List<dynamic>> fetchHistory(String token) async {
    final response = await http.get(
      Uri.parse("$baseUrl/history"),
      headers: {
        "Authorization": "Bearer $token",
      },
    );

    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else if (response.statusCode == 401) {
      throw Exception("Non autorisé");
    } else if (response.statusCode == 403) {
      throw Exception("Accès refusé");
    } else {
      throw Exception("Erreur ${response.statusCode}");
    }
  }
}
