{
  description = "Magic The Gathering: Desktop Companion";
  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs";
    systems.url = "github:nix-systems/default";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = {
    self,
    systems,
    nixpkgs,
    flake-utils,
  }:
    flake-utils.lib.eachDefaultSystem
    (system: let
      pkgs = nixpkgs.legacyPackages.${system};
    in {
      packages.default = pkgs.callPackage ./package.nix {};
      apps.default = {
        type = "app";
        program = "${self.packages.x86_64-linux.default}/bin/mtg-desktop-companion.sh";
      };
    });
}
