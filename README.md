# GIMP-Tracker
This plugin tracks your movement, inventory, equipment, skills and experience and displays it on a website

![alt text](https://i.imgur.com/5QQoa1T.png)

[Video](https://www.youtube.com/watch?v=aXN_TnHZUJI)

## Usage
This plugin does nothing significant on its own, its 1 part of a 3 part system, a backend and a frontend is also required, the installation process its available under the [Wiki](https://github.com/Rachnus/GIMP-Tracker/wiki/) section of this repository 

Once you have an endpoint to connect to with the plugin, head over to the GIMP Tracker settings

![alt text](https://i.imgur.com/P5zylDq.png)
![alt text](https://i.imgur.com/a0x6Wi7.png)

Head over to [https://github.com/Rachnus/osrs-gimp-tracker-frontend](https://github.com/Rachnus/osrs-gimp-tracker-frontend) to see instructions on how to use the website

## Testing
If you wish to test the plugin on an existing site, you may do so using my test website

* Website: [https://rachnus.github.io/osrs-gimp-tracker-frontend-test-site/](https://rachnus.github.io/osrs-gimp-tracker-frontend-test-site/)
* Runelite plugin URL setting: `https://gimptracker-backend.herokuapp.com/`
* Runelite plugin Password setting: `password123`

## Settings
Heres a couple settings
* **URL**: should be the IP & PORT to the back-end
* **Password**: should match the password set by the backend in the `config.js` file
* **Send Data**: if enabled, it will update your character on the website every tick
* **Connect on login**: if enabled, it will automatically connect to the backend upon login
* **Send equipment/skill/inventory/health/prayer/run data**: if enabled, it will send updates about set data

## Status

On the right hand side of runelite, there will be a panel available to manually connect/disconnect to the backend, it will also display your last known connection status, if there was an issue

![alt text](https://i.imgur.com/9It7uoE.png)

## Plugin hub link
[https://runelite.net/plugin-hub/show/gimptracker](https://runelite.net/plugin-hub/show/gimptracker)

## Special thanks to

- Adam, Alexsuperfly & abex @ Runelite for answering some crucial questions 
- Mejrs for helping out with extracting map images
- PH01L for having a ton of useful information on his site https://www.osrsbox.com/

