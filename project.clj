(defproject org.clojars.kacurez/clj-parallels (or (System/getenv "PROJECT_VERSION")  "0.0.0")
  :description "Parallel function execution on a data sequence via clojure/core.async"
  :url "https://github.com/kacurez/clj-parallels"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/core.async "0.4.474"]
                 [org.clojure/clojure "1.9.0"]]
  :repositories [["snapshots" {:url "https://clojars.org/repo"
                               :username :env/clojars_username
                               :password :env/clojars_password}]
                 ["releases" {:url "https://clojars.org/repo"
                              :username :env/clojars_username
                              :password :env/clojars_password
                              :sign-releases false}]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[org.clojure/test.check "0.9.0"]]}})
