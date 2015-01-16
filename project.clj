(defproject com.otherpeoplespixels/nucleus "0.1.0-SNAPSHOT"
  :description "A collection of ClojureScript Utilities"
  :url "http://github.com/OtherPeoplesPixels/nucleus"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2665"]
                 [com.cemerick/clojurescript.test "0.3.3"]
                 [prismatic/plumbing "0.3.5"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [com.cemerick/clojurescript.test "0.3.3"]]

  :source-paths ["src" "target/classes"]

  :clean-targets ["out-test/nucleus" "out-test/nucleus.js"]

  :cljsbuild {:builds [{:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-to "out-test/nucleus.js"
                                   :output-dir "out-test"
                                   :optimizations :whitespace
                                   :cache-analysis true}}]

              :test-commands {"test" ["slimerjs" :runner "out-test/nucleus.js"]}}

  :aliases {"test"      ["cljsbuild" "test"]
            "cleantest" ["do" "clean" ["cljsbuild" "test"]]})
