{ lib, fetchFromGitHub, jre, makeWrapper, maven }:

maven.buildMavenPackage rec {
    pname = "MtgDesktopCompanion";
    version = "2.49";

    src = fetchFromGitHub {
        owner = "nicho92";
        repo = "${pname}";
        rev = "${version}";
        sha256 = "sha256-E8tTR2xcJaAY7IOGQFk748TkDyqOjOJGEqZkq59jze8=";
    };

    mvnHash = "sha256-+9OTlhLLSoxhfjd1EiXLbHhxZBCxJv3QnG3GoREBE7Q=";

    nativeBuildInputs = [
        maven
        makeWrapper
    ];

    buildInputs = [
        jre
    ];

    buildPhase = ''
        mvn -DskipTests clean install
    '';

    installPhase = ''
        mkdir -p $out/bin $out/lib
        mv target/executable/bin/*.sh $out/bin
        mv target/executable/lib $out

        wrapProgram $out/bin/mtg-desktop-companion.sh \
          --set JAVA_HOME ${jre} \
          --prefix PATH : ${jre}/bin
    '';
}
