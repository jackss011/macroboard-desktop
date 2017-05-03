# Macroboard Desktop
Desktop program for the mobile app Macroboard.

## Build dependencies
### Sass
In order to compile *Sass* this app requires the *gem* package **sass**.

Run `gem install sass` in the terminal to install the package. On windows machines *Ruby* must be installed through the [Ruby Installer](https://rubyinstaller.org/).

See the [official Sass website](http://sass-lang.com/install) for a more detailed guide.

### Native
This app uses a native library exposed to *Java* code via *Jna*. The library is located in the **native** folder and it's compiled using *Gradle*.

In order to compile the library the following tool-chain(s) are needed:

| OS        | Tool Chain                 |
| ----------| -------------------------- |
| Windows   | Visual C++ (2010 or later) |
| Mac OSX   | XCode                      |
| Linux     | GCC                        |

See [Gradle native software guide](https://docs.gradle.org/current/userguide/native_software.html) for a more detailed explanation.
