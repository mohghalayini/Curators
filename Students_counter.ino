#include <WiFi.h>

// WiFi network name and password:
const char * networkName = "CIK1679";
const char * networkPswd = "000ef4c15810";

// Room ID on the server 
String roomID = "5700239927279616";

// Internet domain to request from:
const char * hostDomain = "gold-hold-183404.appspot.com";
String subDir = (String)"/rooms/" + roomID + "/edit";
const int hostPort = 80;

// Holds data to be sent to the server
String data = "counter=1";

const int BUTTON_PIN = 0;
const int LED_PIN = 5;

/* Sonar variables definitions */
const int trigPin = 19;
const int echoPin = 21;
long duration;
int distance;

void setup()
{
  // Initilize hardware:
  Serial.begin(115200);
  pinMode(BUTTON_PIN, INPUT_PULLUP);
  pinMode(LED_PIN, OUTPUT);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);

  // Connect to the WiFi network (see function below loop)
  connectToWiFi(networkName, networkPswd);

  digitalWrite(LED_PIN, LOW); // LED off

}

void loop()
{
  detectDistance();
  
  if (distance<4)
  {
    digitalWrite(LED_PIN, HIGH); // Turn on LED
    requestURL(hostDomain, hostPort); // Connect to server
    digitalWrite(LED_PIN, LOW); // Turn off LED
  }
}

void detectDistance ()
{
  digitalWrite(trigPin,LOW);
  delayMicroseconds(2);

  digitalWrite(trigPin,HIGH);
  delayMicroseconds(10);

  digitalWrite(trigPin,LOW);

  duration = pulseIn(echoPin,HIGH);
  distance = duration*0.034/2;
}

void connectToWiFi(const char * ssid, const char * pwd)
{
  int ledState = 0;

  printLine();
  Serial.println("Connecting to WiFi network: " + String(ssid));

  WiFi.begin(ssid, pwd);

  while (WiFi.status() != WL_CONNECTED) 
  {
    // Blink LED while we're connecting:
    digitalWrite(LED_PIN, ledState);
    ledState = (ledState + 1) % 2; // Flip ledState
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.println("WiFi connected!");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

void requestURL(const char * host, uint8_t port)
{
  printLine();
  Serial.println("Connecting to domain: " + String(host));

  // Use WiFiClient class to create TCP connections
  WiFiClient client;
  if (!client.connect(host, port))
  {
    Serial.println("connection failed");
    return;
  }
  Serial.println("Connected!");
  printLine();






  

  // This will send the request to the server
 // client.print((String)"GET / HTTP/1.1\r\n" +
   //            "Host: " + String(host) + "\r\n" +
     //          "Connection: close\r\n\r\n");


// This will send the request to the server
 //client.print((String)"GET /?var1=1&var2=2&var3=3 HTTP/1.1\r\n" +
//           "Host: " + String(host) + "\r\n" +
//           "Connection: close\r\n\r\n");

client.print((String)"POST " + String (subDir) + " HTTP/1.0\r\n" +
"Host: " + String(host) + "\r\n" +
"Content-Type: application/x-www-form-urlencoded" + "\r\n" +
"Content-Length: " + String(data.length()) + "\r\n" +
"\r\n" + data+"\r\n"
"Connection: close\r\n\r\n"); 




     
  unsigned long timeout = millis();
  while (client.available() == 0) 
  {
    if (millis() - timeout > 5000) 
    {
      Serial.println(">>> Client Timeout !");
      client.stop();
      return;
    }
  }

  // Read all the lines of the reply from server and print them to Serial
  while (client.available()) 
  {
    String line = client.readStringUntil('\r');
    Serial.print(line);
  }

  Serial.println();
  Serial.println("closing connection");
  client.stop();
}

void printLine()
{
  Serial.println();
  for (int i=0; i<30; i++)
    Serial.print("-");
  Serial.println();
}

