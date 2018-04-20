# Raspberry Pi Climate Control 
A simple Java program that turns on or off an old push button AC unit when the room reaches a certain temperature + some advanced features.

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0452ea1177494b6aaaf62636ece06d0d)](https://www.codacy.com/app/MarcWoodyard/Raspberry-Pi-Climate-Control?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=MarcWoodyard/Raspberry-Pi-Climate-Control&amp;utm_campaign=Badge_Grade)

<p align="center">
  <img width="460" height="300" src="https://raw.githubusercontent.com/MarcWoodyard/Raspberry-Pi-Climate-Control/master/screenshot.png">
</p>

### Dependencies

- Java
- Raspberry Pi
- DHT11 Temperature & Humidity Sensor
- Servo Motor
- Raspberry Pi Camera
- 5V Fan (Optional)
- [Pi4J](http://pi4j.com/install.html) - Java I/O library for the Raspberry Pi

##### Compile & Run
```sh 
clear
javac -d ./bin -cp ./src/.:./src/lib/javamail/mail.jar:./src/lib/pi4j/lib/* src/*.java
sudo java -cp ./bin:./src/lib/javamail/mail.jar:./src/lib/pi4j/lib/* Controller
```

##### Run at Startup (Optional)
[RC.LOCAL](https://www.raspberrypi.org/documentation/linux/usage/rc-local.md)

### Credits

[Eric Smith](https://stackoverflow.com/questions/28486159/read-temperature-from-dht11-using-pi4j/34976602#34976602) - For writing most of the [DHT11.java](https://github.com/MarcWoodyard/Raspberry-Pi-Climate-Control/blob/master/src/DHT11.java) code.

### Troubleshooting

> Unable to determine hardware version. I see: Hardware: BCM2835

https://github.com/Pi4J/pi4j/issues/319 

**Solution :** 
```sh 
sudo rpi-update 52241088c1da59a359110d39c1875cda56496764
```

