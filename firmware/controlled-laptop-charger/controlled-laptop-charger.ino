const int CONTROLLED_OUT = 2;
const unsigned long ON_STATE_DURATION_MS_MAX = 3600000;

byte parseState = 0;
byte demandedState = 0; 

bool outputIsOn = false; 
unsigned long toggleTimestamp;  

void setup() {
  pinMode(LED_BUILTIN, OUTPUT); 
  pinMode(CONTROLLED_OUT, OUTPUT);
  Serial.begin(9600);
}

void setOutputState(byte demandedState) {
  toggleTimestamp = millis(); 
  if (demandedState == 1) {
    digitalWrite(CONTROLLED_OUT, HIGH);
    digitalWrite(LED_BUILTIN, HIGH);
    outputIsOn = true; 
  }
  else {
    digitalWrite(CONTROLLED_OUT, LOW);
    digitalWrite(LED_BUILTIN, LOW);
    outputIsOn = false; 
  }
}

inline void switchOutputOff() {
  setOutputState(0); 
}

inline unsigned long getCurrentStateDuration() {
  return millis() - toggleTimestamp; 
}

void loop() {  
  if (outputIsOn && (getCurrentStateDuration() > ON_STATE_DURATION_MS_MAX) ) 
     switchOutputOff();

  
  if (Serial.available()) {
    char c = Serial.read();
    switch(parseState) {
      case 0: 
        if (c == '>') parseState = 1; 
        break; 
      case 1: 
        if (c == '1') {
          demandedState = 1;
          parseState = 2; 
        } else if (c == '0') {
          demandedState = 0; 
          parseState = 2; 
        } else parseState = 0; 
        break; 
      
      case 2: 
        if (c == '\n') {
          parseState = 0; 
          setOutputState(demandedState); 
        }
        
    }
  }
}
