(defproject org.clojars.kacurez/clj-parallels (or (System/getenv "PROJECT_VERSION")  "0.0.0")
  :description "Study and experiments with parallelism via async.io"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/core.async "0.3.443"]
                 [org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/test.check "0.9.0"]]
  :repositories [["snapshots" {:url "https://clojars.org/repo"
                               :username :env/clojars_username
                               :password :env/clojars_password}]
                 ["releases" {:url "https://clojars.org/repo"
                              :username :env/clojars_username
                              :password :env/clojars_password
                              :sign-releases true}]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
