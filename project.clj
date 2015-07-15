(def tk-version "0.3.6")
(def ks-version "0.5.3")

(defproject puppetlabs/trapperkeeper-webserver-jetty7 "0.3.4"
  :description "This version is not actively maintained; please use puppetlabs/trapperkeeper-webserver-jetty9."
  ;; Abort when version ranges or version conflicts are detected in
  ;; dependencies. Also supports :warn to simply emit warnings.
  ;; requires lein 2.2.0+.
  :pedantic? :abort
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [puppetlabs/kitchensink ~ks-version]
                 [puppetlabs/trapperkeeper ~tk-version]

                 ;; Jetty Webserver
                 [org.eclipse.jetty/jetty-servlet "7.6.1.v20120215"]
                 [org.eclipse.jetty/jetty-server "7.6.1.v20120215"
                  :exclusions [org.eclipse.jetty.orbit/javax.servlet]]

                 [ring/ring-servlet "1.1.8"]]
                   
  :plugins [[lein-release "1.0.5"]]

  :lein-release {:scm         :git
                 :deploy-via  :lein-deploy}

  :deploy-repositories [["releases" {:url "https://clojars.org/repo"
                                     :username :env/clojars_jenkins_username
                                     :password :env/clojars_jenkins_password
                                     :sign-releases false}]]

  ;; By declaring a classifier here and a corresponding profile below we'll get an additional jar
  ;; during `lein jar` that has all the code in the test/ directory. Downstream projects can then
  ;; depend on this test jar using a :classifier in their :dependencies to reuse the test utility
  ;; code that we have.
  :classifiers [["test" :testutils]]

  :test-paths ["test/clj"]

  :profiles {:dev {:source-paths ["examples/ring_app/src"
                                  "examples/servlet_app/src/clj"]
                   :java-source-paths ["examples/servlet_app/src/java"
                                       "test/java"]
                   :dependencies [[clj-http "0.5.3"]
                                   [puppetlabs/kitchensink ~ks-version :classifier "test"]
                                   [puppetlabs/trapperkeeper ~tk-version :classifier "test"]
                                   [spyscope "0.1.4"]]
                   :injections [(require 'spyscope.core)]}

             :testutils {:source-paths ^:replace ["test/clj"]
                         :java-source-paths ^:replace ["test/java"]}
             :uberjar {:aot [puppetlabs.trapperkeeper.main]
                       :classifiers ^:replace []}}

  :main puppetlabs.trapperkeeper.main
  )

