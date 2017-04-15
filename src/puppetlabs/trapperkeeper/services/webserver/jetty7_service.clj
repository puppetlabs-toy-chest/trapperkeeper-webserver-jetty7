(ns puppetlabs.trapperkeeper.services.webserver.jetty7-service
  (:require
    [clojure.tools.logging :as log]
    [puppetlabs.trapperkeeper.services.webserver.jetty7-core :as core]
    [puppetlabs.trapperkeeper.core :refer [defservice]]))

;; TODO: this should probably be moved to a separate jar that can be used as
;; a dependency for all webserver service implementations
(defprotocol WebserverService
  (add-ring-handler [this handler path])
  (add-servlet-handler [this servlet path] [this servlet path servlet-init-params])
  (add-war-handler [this war-path ctxt-path])
  (join [this]))

(defservice jetty7-service
  "Provides a Jetty 7 web server as a service"
  WebserverService
  [[:ConfigService get-in-config]]
  (init [this context]
    (log/info "Initializing web server.")
    (let [config (or (get-in-config [:webserver])
                    ;; Here for backward compatibility with existing projects
                    (get-in-config [:jetty])
                    {})
         webserver (core/create-webserver config)]
     (assoc context :jetty7-server webserver)))

  (start [this context]
    (log/info "Starting web server.")
    (core/start-webserver (context :jetty7-server))
    context)

  (stop [this context]
    (log/info "Shutting down web server.")
    (core/shutdown (context :jetty7-server))
    context)

  (add-ring-handler [this handler path]
    (let [s ((service-context this) :jetty7-server)]
      (core/add-ring-handler s handler path)))

  (add-servlet-handler [this servlet path]
    (let [s ((service-context this) :jetty7-server)]
      (core/add-servlet-handler s servlet path)))

  (add-servlet-handler [this servlet path servlet-init-context]
    (let [s ((service-context this) :jetty7-server)]
     (core/add-servlet-handler s servlet path servlet-init-context)))

  (add-war-handler [this war-path ctxt-path]
    (let [s ((service-context this) :jetty7-server)]
      (core/add-war-handler s war-path ctxt-path)))

  (join [this]
    (let [s ((service-context this) :jetty7-server)]
      (core/join s))))
