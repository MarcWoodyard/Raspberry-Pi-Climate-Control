# Raspberry Pi Climate Control

A simple Java program that turns on or off an old push button AC unit when the room reaches a certain temperature + some advanced features.

<p align="center">
  <img width="460" height="300" src="https://raw.githubusercontent.com/MarcWoodyard/Raspberry-Pi-Climate-Control/master/screenshot.png">
</p>

### Dependencies

- Java
- Raspberry Pi
- DHT11 Temperature & Humidity Sensor
- Servo Motor
- [Pi4J](http://pi4j.com/install.html) - Java I/O library for the Raspberry Pi

##### Compile & Run
```sh 
clear && sudo javac -d ./bin -cp ./src:./lib/javamail/lib/*:./lib/pi4j/lib/* src/*.java

sudo java -cp ./bin:./lib/javamail/lib/*:./lib/pi4j/lib/* Main [Max Temperature] [Min Temperature] [Sleep Time (Minutes)]
```

##### Compile & Run - One Command
```sh 
clear && sudo javac -d ./bin -cp ./src:./lib/javamail/lib/*:./lib/pi4j/lib/* src/*.java && sudo java -cp ./bin:./lib/javamail/lib/*:./lib/pi4j/lib/* Main 86 77 2
```

##### Run at Startup (Optional)
[RC.LOCAL](https://www.raspberrypi.org/documentation/linux/usage/rc-local.md)

### Credits

[Eric Smith](https://stackoverflow.com/questions/28486159/read-temperature-from-dht11-using-pi4j/34976602#34976602) - For writing most of the [DHT11.java](https://github.com/MarcWoodyard/Raspberry-Pi-Climate-Control/blob/master/src/DHT11.java) code.

### Troubleshooting

> Unable to determine hardware version. I see: Hardware: BCM2835

https://github.com/Pi4J/pi4j/issues/319 

**Solution:** 
```sh 
sudo rpi-update 52241088c1da59a359110d39c1875cda56496764
```
