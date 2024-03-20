(ns sem-bpm-server.core
  (:require
    [fipp.edn :refer [pprint]]
    [clojure.string :as str]
    [compojure.core :refer [defroutes GET POST]]
    [compojure.route :as route]
    [odysseus.coll :refer [intersection?]]
    [odysseus.debug :refer [---]]
    [odysseus.text :refer [unescape-string]]
    [ring.adapter.jetty :as jetty]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.util.response :as resp]
    [ring.util.request :as req]
    [tabtree.parse :refer [parse-tabtree-string id]]
    [tabtree.utils :refer [filter-tabtree]]
    [tabtree.rdf-tabtree :refer [rdf->tabtree]])
  (:gen-class))

(def server (atom nil))

(defroutes core-handler
  (GET "/" [] (resp/resource-response "index.html" {:root ""}))
  (GET "/test/request" request (format "<pre>%s</pre>" (with-out-str (pprint request))))
  (POST "/rdf_to_dot" request
    (let [rdf-string (-> request req/body-string unescape-string)
          tabtree (rdf->tabtree rdf-string)
          _ (--- 111)
          processes (filter-tabtree
                      (fn [item] (--- item) (intersection?
                                                (-> (select-keys item ["a" "type" "rdf/type"]) vals distinct)
                                                "Process"))
                      tabtree)
          _ (--- 222)
          processes (->> processes vals)
          _ (--- 444 processes)]
      {
       :status 200
       :headers {}
       :body processes}))
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
