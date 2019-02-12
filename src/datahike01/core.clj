(ns datahike01.core
  (:require [datahike.api :refer :all :as d]))

;; use the filesystem as storage medium
(def uri "file:///Users/willahh/www/projects/clojure/datahike01/resources/api-test")
(def uri "datahike:file:///Users/willahh/www/projects/clojure/datahike01/resources/api-test")

(def uri #_"datahike:mem:///test"
  "datahike:file:///tmp/api-test"
  #_"datahike:level:///tmp/api-test1")

;; create a database at this place
(create-database uri)

(def conn (connect uri))


(comment
  ;; lets add some data and wait for the transaction
  @(transact conn [{ :db/id 1, :name  "Ivan", :age   15 }
                   { :db/id 2, :name  "Petr", :age   37 }
                   { :db/id 3, :name  "Ivan", :age   37 }
                   { :db/id 4, :age 15 }])
  
  (q '[:find ?e
       :where [?e :name]]
     @conn)
  
  (q '[:find ?name
       :where [:db/name ?name]]
     @conn)
  
  
  ;; Examples from https://github.com/kristianmandrup/datascript-tutorial/blob/master/add_data.md
  @(transact conn [ { :db/id -1
                     :name  "Maksim"
                     :age   45
                     :aka   ["Maks Otto von Stirlitz", "Jack Ryan"] } ])
  
  (let [maksim {:db/id -1
                :name "Maksim"
                :age 45
                :wife -2
                :aka ["Maks Otto von Stirlitz", "Jack Ryan"]}
        anna {:db/id -2
              :name "Anna"
              :age 31
              :husband -1
              :aka ["Anitzka"]}]
    @(transact conn [maksim anna]))
  
  (q '[:find ?e
       ]
     @conn)
  
  (q '[:find ?n ?a
       :where [?e :aka "Maks Otto von Stirlitz"]
       [?e :name ?n]
       [?e :age ?a]]
     @conn)
  
  (q '[:find ?n
       :where [?e :name ?n]
       ]
     @conn)
  
  (q '[:find ?n
       :where [?e :aka ?n]
       [?e :age 45]
       ]
     @conn)

  
  @(transact conn [{ :db/id 1, :person/name  "Ivan", :person/age   15 }
                   { :db/id 2, :person/name  "Petr", :person/age   37 }
                   { :db/id 3, :person/name  "Ivan", :person/age   37 }
                   { :db/id 4, :person/age 15 }
                   ])
  
  (q '[:find ?e
       :where  [?e :person/name "Ivan"]]
     @conn)
  
  
  (q '[:find ?n ?a
       :where [?e :aka "Maks Otto von Stirlitz"]
       [?e :name ?n]
       [?e :age  ?a] 
       ]
     @conn)
  
  
  
  
  ;; you might need to release the connection, e.g. for leveldb
  ;; (release conn)
  ;; (delete-database uri)
  )

(comment
  
  
  
  (def schema {:task-group/tasks {:db/cardinality :db.cardinality/many
                                  :db/valueType   :db.type/ref}
               :offer/task-groups {:db/cardinality :db.cardinality/many
                                   :db/valueType :db.type/ref}
               :offer/name {:db/cardinality :db.cardinality/one
                            :db/unique :db.unique/identity}
               :customer/name {:db/cardinality :db.cardinality/one
                               :db/unique :db.unique/identity}})
  
  
  (comment
    ;; (re)create database
    (d/delete-database uri)
    (d/create-database-with-schema uri schema))
  
  (def conn (d/connect uri))
  
  (d/transact conn [{:db/id (d/tempid -1)
                     :customer/department "IT Services"
                     :customer/name "FunkyHub Startup"
                     :customer/contact ""
                     :customer/street "Hauptstrabe 129"
                     :customer/postal "62811"
                     :customer/city "Hidelberg"
                     :customer/country "Germany"}
                    {:db/id (d/tempid -1)
                     :customer/department ""
                     :customer/name "Schmitt GmbH"
                     :customer/contact "Peter Schmitt"
                     :customer/street "Heinrich-Heine-Str. 182"
                     :customer/postal "38312"
                     :customer/country "Germany"}])
  
  ;; retrieve clients
  (d/q '[:find ?e ?on
         :where
         [?e :customer/name ?on]]
       @conn)
  
  
  (let [task-group-id (d/tempid -1)
        task-ids (vec (for [i (range 2)] (d/tempid -1)))]
    (d/transact db [{:conn/id             (task-ids 0)
                     :task/name         "Adjustment Login"
                     :task/effort       1
                     :task/effort-unit  :hour
                     :task/effort-price 75
                     :task/price-unit   :euro}
                    {:db/id             (task-ids 1)
                     :task/name         "Extend database schema"
                     :task/effort       4
                     :task/effort-unit  :hour
                     :task/effort-price 75
                     :task/price-unit   :euro}
                    {:db/id             task-group-id
                     :task-group/name  "Android App"
                     :task-group/tasks  task-ids}
                    {:db/id             (d/tempid -1)
                     :offer/customer    [:customer/name "FunkyHub Startup"]
                     :offer/name        "Adjustments App Q1 2018"
                     :offer/advisor     "Christian Weilbach"
                     :offer/task-groups [task-group-id]}
                    ]))
  
  
  
  
  (d/q '[:find ?e ?on
         :where
         [?e :offer/name ?on]]
       @conn)
  
  (d/q '[:find (pull ?tg [*])
         :where
         [?tg :task-group/name ?tgn]]
       @conn)
  
  ;; (d/q '[:find ?e
  ;;        :in $
  ;;        ]
  ;;      @conn)
  
  (d/transact conn [["sally" :age 21] 
                    ["fred" :age 42] 
                    ["ethel" :age 42]
                    ["fred" :likes "pizza"] 
                    ["sally" :likes "opera"] 
                    ["ethel" :likes "sushi"]])
  
  (d/q '[:find ?e
         :where [?e :age]]
       @conn)
  
  (d/q '[:find ?e ?on
         :where
         [?e :customer/name ?on]]
       @conn)
  )
