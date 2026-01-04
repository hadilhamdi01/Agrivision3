import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/auth_service.dart';
import '../services/weather_service.dart';
import '../services/location_service.dart';
import 'loginPage.dart';
import 'diseases.dart';
import 'history_page.dart';
// Si tu as une page Profile, importe-la aussi
// import 'profile_page.dart';

// Couleur principale
const Color primaryGreen = Color(0xFF5DB075);

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  Map<String, dynamic>? weather;
  bool loading = true;
  String? token;

  int _currentIndex = 0; // Home = index 0

  @override
  void initState() {
    super.initState();
    initHome();
  }

  Future<void> initHome() async {
    final prefs = await SharedPreferences.getInstance();
    final storedToken = prefs.getString('token');

    if (storedToken == null) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const LoginPage()),
      );
      return;
    }

    token = storedToken;
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
    }
  }

  String iconUrl(String icon) =>
      "https://openweathermap.org/img/wn/$icon@2x.png";

  Widget weatherCard() {
    if (loading) {
      return const Padding(
        padding: EdgeInsets.all(20),
        child: CircularProgressIndicator(),
      );
    }

    if (weather == null) {
      return const Padding(
        padding: EdgeInsets.all(20),
        child: Text("Weather unavailable"),
      );
    }

    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(20),
        gradient: const LinearGradient(
          colors: [Color(0xFF7EC8E3), Color(0xFFA8E6CF)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  const Icon(Icons.location_on, color: Colors.white),
                  const SizedBox(width: 6),
                  Text(
                    weather!['location'],
                    style: const TextStyle(
                        color: Colors.white, fontSize: 16),
                  ),
                ],
              ),
              const SizedBox(height: 6),
              const Text("Today", style: TextStyle(color: Colors.white70)),
              const SizedBox(height: 6),
              Text(
                "${weather!['status'] }",
                style: const TextStyle(color: Colors.white70),
              ),
              const SizedBox(height: 12),
              Text(
                "${weather!['temperature']}°C",
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 34,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          Image.network(iconUrl(weather!['icon']), width: 90),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF4F7F6),
      drawer: appDrawer(context),
      appBar: AppBar(
        backgroundColor: primaryGreen,
        elevation: 0,
        title: const Text("Agrivision",
            style: TextStyle(fontWeight: FontWeight.bold)),
        leading: Builder(
          builder: (context) => IconButton(
            icon: const Icon(Icons.menu),
            onPressed: () => Scaffold.of(context).openDrawer(),
          ),
        ),
        actions: const [
          Icon(Icons.search),
          SizedBox(width: 16),
          Icon(Icons.notifications_none),
          SizedBox(width: 16),
        ],
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Padding(
              padding: EdgeInsets.fromLTRB(20, 20, 20, 8),
              child: Text(
                "Weather Conditions",
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: Colors.green,
                ),
              ),
            ),
            weatherCard(),
            const Padding(
              padding: EdgeInsets.fromLTRB(20, 20, 20, 8),
              child: Text(
                "Agrivision Smart Support",
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                  color: Colors.green,
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: GridView.count(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                crossAxisCount: 2,
                crossAxisSpacing: 16,
                mainAxisSpacing: 16,
                children: const [
                  FeatureCard(
                    title: "History",
                    subtitle: "Get instant farming advice",
                    icon: Icons.chat_bubble_outline,
                  ),
                  FeatureCard(
                    title: "Disease Scanner",
                    subtitle: "Identify plant problems",
                    icon: Icons.search,
                  ),
                  FeatureCard(
                    title: "Community",
                    subtitle: "Ask & share with farmers",
                    icon: Icons.groups,
                  ),
                  FeatureCard(
                    title: "Profile",
                    subtitle: "Manage your account",
                    icon: Icons.person_outline,
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        selectedItemColor: primaryGreen,
        unselectedItemColor: Colors.grey,
        type: BottomNavigationBarType.fixed,
        onTap: (index) {
          if (index == _currentIndex) return;

          switch (index) {
            case 0:
              Navigator.pushReplacement(
                context,
                MaterialPageRoute(builder: (_) => const HomeScreen()),
              );
              break;
            case 1:
              Navigator.pushReplacement(
                context,
                MaterialPageRoute(builder: (_) => const HistoryPage()),
              );
              break;
            case 2:
              Navigator.pushReplacement(
                context,
                MaterialPageRoute(builder: (_) => const DiseasesPage()),
              );
              break;
            case 3:
              // Profile page si existante
              break;
          }
        },
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: "Home"),
          BottomNavigationBarItem(icon: Icon(Icons.history), label: "History"),
          BottomNavigationBarItem(
              icon: Icon(Icons.camera_alt), label: "Diseases"),
          BottomNavigationBarItem(icon: Icon(Icons.person), label: "Profile"),
        ],
      ),
    );
  }

  // ================= DRAWER =================
  Widget appDrawer(BuildContext context) {
    return Drawer(
      child: Column(
        children: [
          DrawerHeader(
            decoration: const BoxDecoration(color: Color(0xFF76AD81)),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: const [
                CircleAvatar(
                  radius: 30,
                  backgroundColor: Colors.white,
                  child: Icon(Icons.person, size: 34, color: Colors.green),
                ),
                SizedBox(height: 12),
                Text("AgriVision",
                    style: TextStyle(
                        color: Colors.white,
                        fontSize: 20,
                        fontWeight: FontWeight.bold)),
                Text("Smart Farming Assistant",
                    style: TextStyle(color: Colors.white70)),
              ],
            ),
          ),
          ListTile(
            leading: const Icon(Icons.home),
            title: const Text("Home"),
            onTap: () {
              Navigator.pushReplacement(
                context,
                MaterialPageRoute(builder: (_) => const HomeScreen()),
              );
            },
          ),
          ListTile(
            leading: const Icon(Icons.cloud),
            title: const Text("Weather"),
          ),
          ListTile(
            leading: const Icon(Icons.chat),
            title: const Text("History"),
            onTap: () {
              Navigator.pushReplacement(
                context,
                MaterialPageRoute(builder: (_) => const HistoryPage()),
              );
            },
          ),
          ListTile(
            leading: const Icon(Icons.camera_alt),
            title: const Text("Diseases"),
            onTap: () {
              Navigator.pushReplacement(
                context,
                MaterialPageRoute(builder: (_) => const DiseasesPage()),
              );
            },
          ),
          ListTile(
            leading: const Icon(Icons.person),
            title: const Text("Profile"),
            onTap: () {
              // Naviguer vers page Profile si existante
            },
          ),
          const Spacer(),
          ListTile(
            leading: const Icon(Icons.logout, color: Colors.red),
            title: const Text("Logout", style: TextStyle(color: Colors.red)),
            onTap: () async {
              await AuthService.logout();
              Navigator.pushAndRemoveUntil(
                context,
                MaterialPageRoute(builder: (_) => const LoginPage()),
                (route) => false,
              );
            },
          ),
          const SizedBox(height: 16),
        ],
      ),
    );
  }
}

// ================= FEATURE CARD =================
class FeatureCard extends StatelessWidget {
  final String title;
  final String subtitle;
  final IconData icon;

  const FeatureCard({
    super.key,
    required this.title,
    required this.subtitle,
    required this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () {
        switch (title) {
          case "Disease Scanner":
            Navigator.pushReplacement(
              context,
              MaterialPageRoute(builder: (_) => const DiseasesPage()),
            );
            break;
          case "History":
            Navigator.pushReplacement(
              context,
              MaterialPageRoute(builder: (_) => const HistoryPage()),
            );
            break;
          case "Profile":
            // Naviguer vers ProfilePage si existante
            break;
        }
      },
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(18),
          color: Colors.white,
          boxShadow: [
            BoxShadow(color: Colors.black.withOpacity(0.06), blurRadius: 10),
          ],
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, size: 48, color: Colors.green),
            const SizedBox(height: 12),
            Text(title,
                style:
                    const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            const SizedBox(height: 6),
            Text(subtitle,
                textAlign: TextAlign.center,
                style: const TextStyle(fontSize: 12, color: Colors.grey)),
          ],
        ),
      ),
    );
  }
}
