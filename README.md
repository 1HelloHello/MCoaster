MCoaster (pronounced: _MC Coaster_) is a fork of [Splinecart](https://modrinth.com/mod/splinecart) that makes you able to build much smoother layouts by allowing for orientations on a 5° basis.

## Alpha Disclaimer
This mod is still in its early development and thereby may have a lot of unresolved issues and bugs. **Always take Backups**. Your builds may also get incompatible with later releases of this mod.

## How it works
All items can be found in the Redstone tab of the creative inventory.

### Track Markers (analogous to Track Ties)
Track marker blocks when placed can be oriented in four degrees of freedom by using the Arrow Tool Items. These are:
- Heading Tool: changes the horizontal direction the track goes in
- Pitching Tool: changes the pitch for when the track goes up and down
- Banking Tool: changes the banking for when the track goes through curves
- Relative Orientation Tool: changes the heading relative to the already applied pitch; is only necessary for more complicated elements

### Track item
The Track item is needed to connect the markers together. Left click with the item on the first marker and right click on the second and all following markers.

### Coaster Cart item
The coaster cart item is needed to ride the layout. Right click a marker block to place the cart at that location. Left click to also automatically enter the cart. The placed cart will retain the velocity that the last cart had when it passed this location (this makes it easy to test the layout from somewhere in between). If you don't want this, shift click instead. When you ride a cart there's a display above your hotbar showing you the speed and G-forces that you experience. The G-forces are not very acurate.

### Magnetic and Chaindrive tracks
To change the track type from normal track to magnetic or chain drive track use the track type tool on the marker in front of the track. You can then change the power level by using the Power Tool. This will set the target speed in km/h. If the power is set to "unset" (which it is by default) it will use the power setting of the first track piece that isn't "unset" that comes before it (usefull when for example the chainlift consists of multiple track pieces that should all have the same speed; this way only the first one in line needs to be set).

### Track appearance
The track style tool can be used in a similar manner to the track type tool, just that it lets you choose between 8 different appearance for the track. You can also color the track by using Minecraft Vanilla dyes. Just right click a marker block to color the following track segment. Shift click to color **all** the track segments that follow and already have the same color.

## Config
To change the confic use
```
/mcoaster config <config option> <value>
```
### The options are:
- _rotate camera_ sets whether or not the camera should rotate according to the motion of the coaster cart. This should be set to false if you experience motion sickness.
- _show debug_ sets whether or not the overlay of the track marker should always be shown.
- _suspended view_ **Experiemental Feature** moves the camera below the track when riding a coaster cart so that it looks like your riding a suspended coaster. The minecart and player themselves will still be rendered normaly.
### Performance stuff:
- _track render distance_ Sets the distance in chunks of how far away tracks will still be rendered
- _track resolution_ Sets how accurately the track is rendered. Lower values will cause a stair stepping effect in tight curves.

## Youtube
You can look at [my youtube channel](https://www.youtube.com/channel/UC1r6bZ07RN6EREQoKkbpkMg) for videos that I've made about this mod.
