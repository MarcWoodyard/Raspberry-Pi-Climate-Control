# Raspberry Pi Climate Control 

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0452ea1177494b6aaaf62636ece06d0d)](https://www.codacy.com/app/MarcWoodyard/Raspberry-Pi-Climate-Control?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=MarcWoodyard/Raspberry-Pi-Climate-Control&amp;utm_campaign=Badge_Grade) 
[![Maintainability](https://api.codeclimate.com/v1/badges/05339f63607ad83ed69d/maintainability)](https://codeclimate.com/github/MarcWoodyard/Raspberry-Pi-Climate-Control/maintainability)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FMarcWoodyard%2FRaspberry-Pi-Climate-Control.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FMarcWoodyard%2FRaspberry-Pi-Climate-Control?ref=badge_shield)

A very specilized program I wrote for a client that turns an old push button AC unit on and off when the room reaches a certain temperature. It uses a DHT11 temperature sensor to detect when to room is getting too hot, and then moves the attached servo to turn the AC unit remote on. The program then takes a picture of the remote to determine if the system is running (should be in a dark box so if the system is on, the remote should have display that turns on). And when the system is off, the lights on the remote should also be off so when the camera takes a picture, the only color in the picure should be the color black. And then the whole process repeates.

<p align="center">
  <img src="https://raw.githubusercontent.com/MarcWoodyard/Raspberry-Pi-Climate-Control/master/screenshot.png">
</p>

### Dependencies

- Java
- Raspberry Pi 2 B+ or above
- DHT11 Temperature & Humidity Sensor
- Servo Motor
- Raspberry Pi Camera

##### Run
```sh 
$ java -jar Raspberry-Pi-Climate-Control.jar (Verbose Logging (True/False)) (Web Server Port (8080) (Test Sensors (True/False)))
```

##### Run at Startup (Optional)
Add the run command to your [RC.LOCAL](https://www.raspberrypi.org/documentation/linux/usage/rc-local.md) file. Don't forget to add a "&" after the command so it runs in the background.

### Credits

[Eric Smith](https://stackoverflow.com/questions/28486159/read-temperature-from-dht11-using-pi4j/34976602#34976602) - For writing most of the [DHT11.java](https://github.com/MarcWoodyard/Raspberry-Pi-Climate-Control/blob/master/src/sensors/DHT11.java) code.

### Troubleshooting

> Unable to determine hardware version. I see: Hardware: BCM2835

https://github.com/Pi4J/pi4j/issues/319 

**Solution :** 
```sh 
sudo rpi-update 52241088c1da59a359110d39c1875cda56496764
```

