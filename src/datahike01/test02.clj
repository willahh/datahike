(ns datahike01.test02
  (:require [datahike.api :refer :all :as d]))

;; use the filesystem as storage medium
(def uri #_"datahike:mem:///test"
  "datahike:file:///tmp/api-test"
  #_"datahike:level:///tmp/api-test1")

;; create a database at this place
(d/delete-database uri)
(create-database uri)

(def conn (connect uri))

(d/transact conn [{:user/id 1 :user/name "Will" :user/company "Home"}
                  {:user/id 2 :user/name "User" :user/company "Company a"}
                  {:user/id 3 :user/name "User 2" :user/company "Company a"}
                  {:group/id 1 :group/name "Group a"}
                  {:group/id 2 :group/name "Group a"}
                  {:user/id 1 :user/name "Will updated"}
                  {:user/id 1 :user/name "Will updated 2"}])

(d/q '[:find ?e ?on
       :where
       [?e :user/name ?on]]
     @conn)

(d/q '[:find ?e ?name ?company
       :where
       [?e :user/company ?company]
       [?name :user/name ]]
     @conn)

(q '[:find ?id ?name ?company
     :where
     [_ :user/name ?name]
     [_ :user/id ?id]
     [_ :user/company ?company]]
   @conn)

(d/q '[:find ?name
       :in $
       :where
       [$ _ :user/name ?name]]
     @conn)

(d/q '[:find ?e ?on
       :where
       [?e :group/id ?on]]
     @conn)