(ns sem-bpm-client.core
  (:require
    [ajax.core :refer [GET POST]])
  (:require-macros [sem-bpm-client.macros :refer [inline-file-content]]))

(def server-url "http://localhost:3001")

(defn init [& args]
  (let [example-button (js/document.getElementById "example_button")
        generate-button (js/document.getElementById "generate_button")]
    (.addEventListener example-button "click"
      (fn [e]
        (let [code-textarea (js/document.getElementById "code-textarea")
              content (inline-file-content "../rdf/target/chart_1.ttl")]
          (set! (.-innerHTML code-textarea) content))))
    (.addEventListener generate-button "click"
      (fn [e]
        (let [code-textarea (js/document.getElementById "code-textarea")
              rdf-result-block (js/document.getElementById "pills-rdf-content")]
          (POST
            (str server-url "/generate")
            {
              :body (.-innerHTML code-textarea)
              :handler (fn [response]
                         ; (js/console.log response)
                         ; (remove-class! rdf-result-block "placeholder")
                         ; (add-class! rdf-result-block "code")
                         (.setAttribute rdf-result-block "class" "code")
                         (set! (.-innerHTML rdf-result-block) response))}))))))

(.addEventListener js/document "DOMContentLoaded" init)
