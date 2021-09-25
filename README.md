# GIMP-Tracker
This plugin tracks your movement, inventory, equipment, skills and experience and displays it on a website

![alt text](https://i.imgur.com/5QQoa1T.png)

[Video](https://www.youtube.com/watch?v=Xqplzc1xXfQ)

## Usage
This plugin does nothing significant on its own, its 1 part of a 3 part system, a backend and a frontend is also required, the installation process its available under the [Wiki](https://github.com/Rachnus/GIMP-Tracker/wiki/) section of this repository 

Once you have an endpoint to connect to with the plugin, head over to the GIMP Tracker settings

![alt text](https://i.imgur.com/P5zylDq.png)
![alt text](https://i.imgur.com/a0x6Wi7.png)

## Settings
Heres a couple settings
* URL: should be the IP & PORT to the back-end
* Password: should match the password set by the backend in the `config.js` file
* Send Data: if enabled, it will update your character on the website every tick
* Connect on login: if enabled, it will automatically connect to the backend upon login

## Status

On the right hand side of runelite, there will be a panel available to manually connect/disconnect to the backend, it will also display your last known connection status, if there was an issue

![alt text](https://i.imgur.com/9It7uoE.png)


