import 'dart:convert';
import 'package:agrivision/screens/loginPage.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

void main() {
  runApp(const MyApp());
}

const String keycloakBaseUrl = 'http://localhost:8085';
const String realm = 'agrivision';
const String clientId = 'plant-service';
const String clientSecret = 'uBEeIQmv8lBK7UZ29mPj0Al2bDqYvnyI';
const String gatewayBaseUrl = 'http://localhost:8083';

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'AgriVision Auth Test',
      home: const LoginPage(),
    );
  }
}
