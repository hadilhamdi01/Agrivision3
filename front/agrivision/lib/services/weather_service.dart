import 'dart:convert';
import 'package:http/http.dart' as http;

class WeatherService {
  static const String baseUrl = 'http://localhost:8083'; 

  static Future<Map<String, dynamic>> getWeather(
      double lat, double lon, String token) async {
    final url = Uri.parse("$baseUrl/weather?lat=$lat&lon=$lon");

    final response = await http.get(url, headers: {
      'Authorization': 'Bearer $token',
    });

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception("Erreur WeatherService: ${response.body}");
    }
  }
}
