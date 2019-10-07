#include <Wire.h>
#include <SPI.h>
#include <Adafruit_PN532.h>

Adafruit_PN532 nfc(53);
#if defined(ARDUINO_ARCH_SAMD)
#define Serial SerialUSB
#endif

void setup(void) {
#ifndef ESP8266
  while (!Serial);
#endif
  Serial.begin(115200);
  Serial.println("Hello!");
  pinMode(7, OUTPUT);
  pinMode(8, OUTPUT);
  digitalWrite(8, OUTPUT);
  nfc.begin();
  uint32_t versiondata = nfc.getFirmwareVersion();
  if (! versiondata) {
    Serial.print("Didn't find PN53x board");
    while (1); // halt
  }
  // Got ok data, print it out!
  Serial.print("Found chip PN5"); Serial.println((versiondata >> 24) & 0xFF, HEX);
  Serial.print("Firmware ver. "); Serial.print((versiondata >> 16) & 0xFF, DEC);
  Serial.print('.'); Serial.println((versiondata >> 8) & 0xFF, DEC);

  // configure board to read RFID tags
  nfc.SAMConfig();

  Serial.println("Waiting for an ISO14443A Card ...");
}


void loop(void) {

  digitalWrite(8, HIGH);
  nfc.begin();
  nfc.SAMConfig();
  
  uint8_t success;
  uint8_t uid[] = { 0, 0, 0, 0, 0, 0, 0 };  // Buffer to store the returned UID
  uint8_t data_a[] = {0x70, 0x6C, 0x6F, 0x6B, 0x69, 0x6E, 0x67, 0x50, 0x4C, 0x4F, 0x4B, 0x49, 0x4E, 0x47, 0x6D, 0x6B};
  uint8_t uidLength;                        // Length of the UID (4 or 7 bytes depending on ISO14443A card type)

  success = nfc.readPassiveTargetID(PN532_MIFARE_ISO14443A, uid, &uidLength);

  if (success) {
    // Display some basic information about the card
    Serial.println("Found an ISO14443A card");
    Serial.print("  UID Length: "); Serial.print(uidLength, DEC); Serial.println(" bytes");
    Serial.print("  UID Value: ");
    nfc.PrintHex(uid, uidLength);
    Serial.println("");

    if (uidLength == 4)
    {
      // Trying to authenticate block 4 with default KEYA value
      uint8_t keya[6] = { 0xD3, 0xF7, 0xD3, 0xF7, 0xD3, 0xF7 };
      success = nfc.mifareclassic_AuthenticateBlock(uid, uidLength, 4, 0, keya);
      if (success)
      {
        uint8_t data[16];
        success = nfc.mifareclassic_ReadDataBlock(6, data);
        if (success)
        {
          nfc.PrintHexChar(data, 16);
          Serial.println("");

          bool corr = true;
          for(int i=0; i!=16; ++i){
            if(data[i] != data_a[i]) corr = false;
          }

          uint8_t data_w[16] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
          success = nfc.mifareclassic_WriteDataBlock(6, data_w);
          if (success && corr) {
            digitalWrite(7, HIGH);
            delay(500);
            digitalWrite(7, LOW);
            Serial.println("Block 6 reseted");
          } else Serial.println("not data correct.");
          
        }
        else Serial.println("Ooops ... unable to read the requested block.  Try another key?");
      } else Serial.println("Ooops ... authentication failed: Try another key?");
    }
  }
  digitalWrite(8, LOW);
  delay(2500);
}
