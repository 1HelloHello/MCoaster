MCoaster (pronounced: _MC Coaster_) is a fork of [Splinecart](https://modrinth.com/mod/splinecart) that makes you able to build much smoother layouts by allowing for orientations on a 5° basis.

## Disclaimer
This mod is still in its early development and thereby may have a lot of unresolved issues and bugs. **Always take Backups**. Your builds may also get incompatible with later releases of this mod.

# How it works
All items can be found in the MCoaster tab of the creative inventory (switch to second page).

## Track Markers (analogous to Track Ties)
Track marker blocks when placed can be oriented in four degrees of freedom by using the Arrow Tool Items. These are:
- Heading Tool: changes the horizontal direction the track goes in
- Pitching Tool: changes the pitch for when the track goes up and down
- Banking Tool: changes the banking for when the track goes through curves
- Relative Orientation Tool: changes the heading relative to the already applied pitch; is only necessary for more complicated elements

## Track item
The Track item is needed to connect the markers together. Left click with the item on the first marker and right click on the second and all following markers.

## Coaster Cart item
The coaster cart item is needed to ride the layout. Right click a marker block to place the cart at that location. Left click to also automatically enter the cart. The placed cart will retain the velocity that the last cart had when it passed this location (this makes it easy to test the layout from somewhere in between). If you don't want this, shift click instead.

## Magnetic and Chaindrive tracks
To change the track type from normal track to magnetic or chain drive track use the track type tool on the marker in front of the track. You can then change the power level by using the Power Tool. This will set the target speed in km/h. If the power is set to "unset" (which it is by default) it will use the power setting of the first track piece that isn't "unset" that comes before it (usefull when for example the chainlift consists of multiple track pieces that should all have the same speed; this way only the first one in line needs to be set). In a similar manner you can change the track setting by using the Track Setting Tool. For a magnetic track this will set the acceleration in m/s².

## Track appearance
The track style tool can be used in a similar manner to the track type tool, just that it lets you choose between 8 different appearance for the track. You can also color the track by using Minecraft Vanilla dyes. Just right click a marker block to color the following track segment. Shift click to color **all** the track segments that follow and already have the same color.

## Triggers (advanced feature)
Triggers can be used to create block zones, shuttle coasters and much more, by having a marker block change the power and setting of another marker block when driven over.
To create a Trigger (using the Trigger Tool item)
1. Left click the marker block that should be changed
2. Right click the marker block that when driven over should change the power and setting of the first one. This will store the position, current power, and current setting of the first one into the second one.

Shift right click a marker to removed all stored triggers.

In most cases you want two markers the change the power / setting of another marker back and forth to, for example turn on or off a chainlift.

# Config
## Client
To change the client confic use
```
/mcoaster config <config option> <value>
```
### The options are:
- _rotate camera_ sets whether or not the camera should rotate according to the motion of the coaster cart. This should be set to false if you experience motion sickness.
- _show debug_ sets whether or not the overlay of the track marker should always be shown.
- _suspended view_ **Experiemental Feature** moves the camera below the track when riding a coaster cart so that it looks like your riding a suspended coaster. The minecart and player themselves will still be rendered normaly.
### Speed info:
- _show speed info_ If set to false, hides the speed info display when riding a coaster cart.
- _show speed info peak_ If set to true, shows the peak speed (highest and lowest) that the cart had reached on a hill or vally.
- _show speed info force_ If set to true, shows the force in G that the cart experiences.
- _show imperial_ If set to true, shows all the speed values the actionbar in imperial (doesn't change values entered with a Power Tool).
### Performance:
- _track render distance_ Sets the distance in chunks of how far away tracks will still be rendered
- _track resolution_ Sets how accurately the track is rendered. Lower values will cause a stair stepping effect in tight curves.

## Server (gamerules)
- _triggerOutput_ If set to false, hides status messages sent when triggers get activated.
- _coasterFriction_ Controls the friction, the cart experiences when rolling on a track. Default is 20. When set to 0, no friction is experienced.

# Youtube
You can look at [my youtube channel](https://www.youtube.com/channel/UC1r6bZ07RN6EREQoKkbpkMg) for videos that I've made about this mod.
