# Nucleus

A collection of ClojureScript utilities.

* * *
WARNING: Work in progress. Not yet ready for production use.
* * *

## Usage

Clone this repository and install it locally using leiningen:

```
git clone https://github.com/otherpeoplespixels/nucleus
cd nucleus
lein install
```

Also, include the following dependency in your project.clj:

```
[com.otherpeoplespixels/nucleus "0.1.0-SNAPSHOT"]
```

## Tests

To run the tests you will need to have [slimerjs][slimerjs] installed and
properly configured.

Install and configure slimerjs on OSX:

```sh
brew install slimerjs --without-xulrunner
export SLIMERJSLAUNCHER=/Applications/Firefox.app/Contents/MacOS/firefox
```

Then run the tests:

```sh
lein test
```

## Thanks!

This code was developed with the support of [otherpeoplespixels][opp].

## License

Copyright © 2014 deeperbydesign and all [contributors][contrib].

Distributed under the [Eclipse Public License][license].

[opp]: http://www.otherpeoplespixels.com
[license]: http://www.eclipse.org/legal/epl-v10.html
[contrib]: https://github.com/otherpeoplespixels/nucleus/contributors
[slimerjs]: http://slimerjs.org/
