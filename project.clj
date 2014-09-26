(defproject com.otherpeoplespixels/nucleus "0.1.0-SNAPSHOT"
  :description "A collection of ClojureScript Utilities"
  :url "http://github.com/OtherPeoplesPixels/nucleus"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]
                                  [org.clojure/clojurescript "0.0-2202"]]
                   :plugins [[lein-cljsbuild "1.0.3"]
                             [com.cemerick/clojurescript.test "0.3.1"]]

                   :cljsbuild {:builds [{:id "test"
                                         :source-paths ["src" "test"]
                                         :compiler {:optimizations :whitespace
                                                    :output-to "target/test.js"}}]
                               :test-commands {"test" ["phantomjs" :runner "target/test.js"]}}}}

  :aliases {"test"      ["cljsbuild" "test"]
            "cleantest" ["do" ["cljsbuild" "clean"] ["cljsbuild" "test"]]})
