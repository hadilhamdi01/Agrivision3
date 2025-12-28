import 'dart:convert';
import 'package:http/http.dart' as http;

class AuthService {
  static const String baseUrl = 'http://localhost:8085';
  static const String realm = 'agrivision';
  static const String clientId = 'agrivision-mobile';

  static Future<String?> login(String username, String password) async {
    final url = Uri.parse(
      '$baseUrl/realms/$realm/protocol/openid-connect/token',
    );

    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: {
        'grant_type': 'password',
        'client_id': clientId,
        'username': username,
        'password': password,
      },
    );

    print('STATUS: ${response.statusCode}');
    print('BODY: ${response.body}');

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data['access_token'];
    }
    return null;
  }
}
