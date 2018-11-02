(ns luminus.async
  (:require [immutant.web.async :as async]))

(set! *warn-on-reflection* true)

(defn- report-error [^Throwable error]
  {:status 500
   :headers {"Content-Type" "text/plain; charset=utf-8"}
   :body
   (str "HTTP ERROR 500\n"
        "Reason: " (.getMessage error))})

(defn- on-open [channel request handler]
  (let [respond (fn [data]
                  (async/send! channel
                               data
                               {:close? true}))
        reject (fn [error]
                 (respond (report-error error)))]
    (handler request respond reject)))

(defn wrap [handler]
  (fn [request]
    (async/as-channel request
                      :on-open #(on-open % request handler))))
