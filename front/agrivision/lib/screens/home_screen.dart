import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/auth_service.dart';
import '../services/weather_service.dart';
import '../services/location_service.dart';
import 'loginPage.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  Map<String, dynamic>? weather;
  bool loading = true;
  String? token;

  // Liste des plantes
  static const List<Map<String, String>> plants = [
    {
      "name": "Tomato",
      "description": "Needs lots of sunlight",
      "image": "https://picsum.photos/seed/tomato/200/200"
    },
    {
      "name": "Lettuce",
      "description": "Keep soil moist",
      "image": "https://picsum.photos/seed/lettuce/200/200"
    },
    {
      "name": "Carrot",
      "description": "Grow underground",
      "image": "https://picsum.photos/seed/carrot/200/200"
    },
    {
      "name": "Cucumber",
      "description": "Requires trellis",
      "image": "https://picsum.photos/seed/cucumber/200/200"
    },
  ];

  @override
  void initState() {
    super.initState();
    initHome();
  }

  /// Charger le token et la météo
  void initHome() async {
    final prefs = await SharedPreferences.getInstance();
    final storedToken = prefs.getString('token');

    if (storedToken == null) {
      Navigator.pushReplacement(
          context, MaterialPageRoute(builder: (_) => const LoginPage()));
      return;
    }

    setState(() => token = storedToken);
    await loadWeather();
  }

  Future<void> loadWeather() async {
    try {
      final position = await LocationService.getLocation();
      final data = await WeatherService.getWeather(
        position.latitude,
        position.longitude,
        token!,
      );

      setState(() {
        weather = data;
        loading = false;
      });
    } catch (e) {
      setState(() => loading = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(e.toString())),
      );
    }
  }

  String iconUrl(String icon) =>
      "https://openweathermap.org/img/wn/$icon@2x.png";

  Widget weatherBloc() {
    if (loading) {
      return const Padding(
        padding: EdgeInsets.all(20),
        child: CircularProgressIndicator(),
      );
    }

    if (weather == null) {
      return const Padding(
        padding: EdgeInsets.all(20),
        child: Text("Impossible de récupérer la météo",
            style: TextStyle(color: Colors.red)),
      );
    }

    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(color: Colors.black.withOpacity(0.08), blurRadius: 12, offset: const Offset(0, 6)),
        ],
      ),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              Column(
                children: [
                  const Icon(Icons.location_on, size: 28, color: Colors.orange),
                  const SizedBox(height: 6),
                  const Text("Location"),
                  const SizedBox(height: 4),
                  Text(weather!['location'], style: const TextStyle(fontWeight: FontWeight.bold)),
                ],
              ),
              Column(
                children: [
                  const Icon(Icons.water_drop, size: 28, color: Color.fromARGB(255, 0, 166, 255)),
                  const SizedBox(height: 6),
                  const Text("Humidity"),
                  const SizedBox(height: 4),
                  Text("${weather!['humidity']}%", style: const TextStyle(fontWeight: FontWeight.bold)),
                ],
              ),
              Column(
                children: [
                  const Icon(Icons.air, size: 28, color: Colors.grey),
                  const SizedBox(height: 6),
                  const Text("Wind"),
                  const SizedBox(height: 4),
                  Text("${weather!['windSpeed']} km/h", style: const TextStyle(fontWeight: FontWeight.bold)),
                ],
              ),
            ],
          ),
          const SizedBox(height: 12),
          Image.network(iconUrl(weather!['icon']), width: 80, height: 80),
          const SizedBox(height: 6),
          Text(weather!['plantAdvice'], textAlign: TextAlign.center),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF0F8E8),
      body: SafeArea(
        child: Column(
          children: [
            // Top menu
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Icon(Icons.menu, size: 30, color: Colors.green),
                  IconButton(
                    icon: const Icon(Icons.logout, color: Colors.redAccent),
                    onPressed: () async {
                      await AuthService.logout();
                      Navigator.pushReplacement(
                        context,
                        MaterialPageRoute(builder: (_) => const LoginPage()),
                      );
                    },
                  ),
                ],
              ),
            ),

            // Bloc météo dynamique
            weatherBloc(),

            // Titre Plants
            const Padding(
              padding: EdgeInsets.fromLTRB(20, 10, 20, 12),
              child: Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  "My Plants",
                  style: TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                    color: Colors.green,
                  ),
                ),
              ),
            ),

            // Liste des plants (grille)
            Expanded(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 20),
                child: GridView.builder(
                  itemCount: plants.length,
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 2,
                    mainAxisSpacing: 16,
                    crossAxisSpacing: 16,
                    childAspectRatio: 0.85,
                  ),
                  itemBuilder: (context, index) {
                    final plant = plants[index];
                    return _PlantCard(
                      name: plant["name"]!,
                      description: plant["description"]!,
                      imageUrl: plant["image"]!,
                    );
                  },
                ),
              ),
            ),
          ],
        ),
      ),

      // Bottom navigation
      bottomNavigationBar: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 8,
              offset: const Offset(0, -3),
            ),
          ],
        ),
        padding: const EdgeInsets.symmetric(vertical: 12),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: const [
            Icon(Icons.notifications_none, size: 32, color: Colors.green),
            Icon(Icons.local_florist, size: 32, color: Colors.orange),
            Icon(Icons.home, size: 32, color: Colors.green),
            Icon(Icons.person_outline, size: 32, color: Colors.green),
          ],
        ),
      ),
    );
  }
}

class _PlantCard extends StatelessWidget {
  final String name;
  final String description;
  final String imageUrl;

  const _PlantCard({
    required this.name,
    required this.description,
    required this.imageUrl,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(20),
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.08),
            blurRadius: 10,
            offset: const Offset(0, 5),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ClipRRect(
            borderRadius: const BorderRadius.vertical(top: Radius.circular(20)),
            child: Image.network(
              imageUrl,
              height: 100,
              width: double.infinity,
              fit: BoxFit.cover,
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(name,
                    style: const TextStyle(
                        fontSize: 16, fontWeight: FontWeight.bold)),
                const SizedBox(height: 6),
                Text(
                  description,
                  style: TextStyle(fontSize: 13, color: Colors.grey[700]),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
