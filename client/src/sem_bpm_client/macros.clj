(ns sem-bpm-client.macros)

(defmacro inline-file-content [filepath]
  (slurp filepath))
