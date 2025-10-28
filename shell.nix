{
  pkgs ? import <nixpkgs> { },
}:

pkgs.mkShell {
  name = "minecraft-dev-env";

  nativeBuildInputs = [
    pkgs.gradle
    pkgs.openjdk21
  ]; # or openjdk17, depending on MC version

  buildInputs = with pkgs; [
    libGL
    # libX11
    # libXext
    # libXcursor
    # libXrandr
    # libXxf86vm
    glfw
    openal
    udev
    pulseaudio
    (lib.getLib stdenv.cc.cc)
  ];

  shellHook = ''
    export LD_LIBRARY_PATH=${
      pkgs.lib.makeLibraryPath [
        pkgs.libGL
        # pkgs.libX11
        # pkgs.libXext
        # pkgs.libXcursor
        # pkgs.libXrandr
        # pkgs.libXxf86vm
        pkgs.glfw
        pkgs.openal
        pkgs.udev
        pkgs.pulseaudio
        (pkgs.lib.getLib pkgs.stdenv.cc.cc)
        pkgs.xorg.libXrender
      ]
    }:$LD_LIBRARY_PATH
  '';
}
