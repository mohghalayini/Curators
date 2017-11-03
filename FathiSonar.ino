
#define trigPin1 19
#define echoPin1 21
 
#define trigPin2 22
#define echoPin2 18
 
int hit1 = 0, hit2 = 0;
int numEntries = 0, numExits = 0;
 
 
// include the library code:
//#include <LiquidCrystal.h>
 
// initialize the library with the numbers of the interface pins
//LiquidCrystal lcd(12, 11, 5, 4, 3, 2);
 
void setup() {
  Serial.begin(9600);
  // set up the LCD's number of columns and rows:
  //lcd.begin(16, 2);
  //Print a message to the LCD.
  //lcd.print("Entity Count:");
 
  //sensors
  pinMode(trigPin1, OUTPUT);
  pinMode(echoPin1, INPUT);
  pinMode(trigPin2, OUTPUT);
  pinMode(echoPin2, INPUT);
 
}
 
void loop() {
  // set the cursor to column 0, line 1
  //lcd.setCursor(0, 1);
  
  int duration2, distance2;

  hit1 = 0;
  hit2 = 0;

  //check sensor1
  int duration, distance;
  digitalWrite(trigPin1, LOW);
  delayMicroseconds (2);   // Inactive period
  digitalWrite (trigPin1, HIGH);
  delayMicroseconds (5);   // Active period
  digitalWrite (trigPin1, LOW);
  duration = pulseIn (echoPin1, HIGH);
  distance = (duration/2) / 29.1;
 
  if(distance < 125){
    Serial.println("Entering...");
    hit1 = 1;
  }

  if(hit1 == 0)
  {
    //check sensor2
    digitalWrite(trigPin2, LOW);
    delayMicroseconds (2);
    digitalWrite (trigPin2, HIGH);
    delayMicroseconds (5);
    digitalWrite (trigPin2, LOW);
 
    duration2 = pulseIn (echoPin2, HIGH);
    distance2 = (duration2 / 2) / 29.1;
 
    if(distance2 < 125){
      Serial.println("Exitting...");
     hit2 = 1;
   }
  }

  Serial.print("Hit1: ");
  Serial.print(hit1);
  Serial.print("\n");


  Serial.print("Hit2: ");
  Serial.print(hit2);
  Serial.print("\n");


 
  //entry was triggered, scan exit until exit is hit
  while(hit1 == 1 && hit2 == 0){
    //Serial.println("Entered first loop");

    delay(250);

    
    //check sensor2
    int dur, dist;
    digitalWrite(trigPin2, LOW);
    delayMicroseconds (2);
    digitalWrite (trigPin2, HIGH);
    delayMicroseconds (5);
    digitalWrite (trigPin2, LOW);
   
    dur = pulseIn (echoPin2, HIGH);
    dist = (dur / 2) / 29.1;
    

    if(dist < 125){
      //reset the two values
      hit1 = 0;
      hit2 = 0;
      numEntries++;
      Serial.println("\tENTERED");
      delay(250);
      break;
    }
  }
 
 
  //exit was triggered, scan entry until entry is hit
  while(hit1 == 0 && hit2 == 1){
    //Serial.println("Entered second loop");

    delay(250);

    //check sensor2
    int dur, dist;
    digitalWrite(trigPin1, LOW);
    delayMicroseconds (2);
    digitalWrite (trigPin1, HIGH);
    delayMicroseconds (5);
    digitalWrite (trigPin1, LOW);
   
    dur = pulseIn (echoPin1, HIGH);
    dist = (dur / 2) / 29.1;


    if(dist < 125){
      //reset the two values
      hit1 = 0;
      hit2 = 0;
      numExits++;
      Serial.println("\tEXITED");
      delay(250);
      break;
    }
  }
 
  //DEBUGGING
  Serial.print(distance);
  Serial.print("-----");
  Serial.print(distance2);
  Serial.println();
  
 
 
  // print the number of seconds since reset:
  //lcd.print(numEntries - numExits);
 
  delay(500);
}

