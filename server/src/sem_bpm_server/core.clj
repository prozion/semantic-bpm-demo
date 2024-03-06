(ns sem-bpm-server.core
  (:require
    [fipp.edn :refer [pprint]]
    [compojure.core :refer [defroutes GET POST]]
    [compojure.route :as route]
    [ring.adapter.jetty :as jetty]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.util.response :as resp]
    [ring.util.request :as req]
    [tabtree.parse :refer [parse-tabtree-string]]
    [tabtree.tabtree-rdf :refer [tabtree->rdf]])
  (:gen-class))

(def server (atom nil))

(defroutes core-handler
  (GET "/" [] (resp/resource-response "index.html" {:root ""}))
  (GET "/test/ping" [] {
                        :status 200
                        :headers {}
                        :body "Hello world"})
  (GET "/test/request" request (format "<pre>%s</pre>" (with-out-str (pprint request))))
  (POST "/generate" request
    (let [tabtree-string (req/body-string request)
          tabtree (parse-tabtree-string tabtree-string {:hi-spacer #"\."})
          rdf (tabtree->rdf tabtree {:use-basic-ontologies false})]
      {
       :status 200
       :headers {}
       :body rdf}))
  (route/resources "/" {:root ""})
  (route/not-found "Not found!"))

(defn start-server []
  (reset! server
          (jetty/run-jetty
            (wrap-cors
              core-handler
              :access-control-allow-origin #".*"
              :access-control-allow-methods [:get :post])
            {
              :port 3001
              :join? false})))

(defn stop-server []
  (when-some [s @server]
    (.stop s)
    (reset! server nil)))

(defn -main [& args]
  (case (first args)
    "start" (start-server)
    "stop" (stop-server)
    nil))
