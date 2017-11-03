/*******************************************************************************/
/*macro definitions of PIR motion sensor pin and LED pin*/
#define PIR_MOTION_SENSOR1 21//Use pin 8 to receive the signal from the module
#define LED1    18//the Grove - LED is connected to D4 of Arduino
#define PIR_MOTION_SENSOR2 19
#define LED2 23

void setup()
{
    Serial.begin(9600);
    pinsInit();
}

void loop()
{
  delay(50);
    if(firstSEN())//if it detects the moving people?
    {
    turnOnLED1();
    }
    else
    turnOffLED1();

    
    /*if(secondSEN())//if it detects the moving people?
    {
    turnOnLED2();
    }
    else
    turnOffLED2();*/
}
void pinsInit()
{
    pinMode(PIR_MOTION_SENSOR1, INPUT);
    pinMode(PIR_MOTION_SENSOR2, INPUT);
    pinMode(LED1,OUTPUT);
    pinMode(LED2,OUTPUT);
}
void turnOnLED1()
{
    digitalWrite(LED1,HIGH);
}
void turnOffLED1()
{
    digitalWrite(LED1,LOW);
}

void turnOnLED2()
{
    digitalWrite(LED2,HIGH);
}
void turnOffLED2()
{
    digitalWrite(LED2,LOW);
}

int oldSen1 = LOW;
/***************************************************************/
/*Function: Detect whether anyone moves in it's detecting range*/
/*Return:-boolean, ture is someone detected.*/
boolean firstSEN()
{
    int sen1 = digitalRead(PIR_MOTION_SENSOR1);
       
    Serial.print("Sensor1: ");

    if(sen1 == HIGH && oldSen1 == LOW)//if the sensor value is HIGH?
    {
      
        Serial.println(sen1);
        oldSen1 = sen1;
        Serial.print("Old Sen1: ");
        Serial.println(oldSen1);

        return true;//yes,return ture
    }
    else
    {
        Serial.println(sen1);
        oldSen1 = sen1;
        return false;//no,return false
    }

} 

int oldSen2;
boolean secondSEN()
{
    int sen2 = digitalRead(PIR_MOTION_SENSOR2);
           
    Serial.print("Sensor2:");

    if(sen2 == HIGH && oldSen2 == LOW)//if the sensor value is HIGH?
    {
        Serial.println(sen2);
        return true;//yes,return ture
    }
    else
    {
        Serial.println(sen2);
        return false;//no,return false
    }
    
    oldSen2 = sen2;
} 
