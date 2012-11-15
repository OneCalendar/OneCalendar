package dao

import com.mongodb.casbah.Imports._

trait MongoDbModel[T] {
    def collectionName: String

    def write(t: T): DBObject

    def read(dbo: DBObject): T
}

trait MongoOperations {
    def save[T](t: T)(implicit tc: MongoDbModel[T], coll: String => MongoCollection): Unit = {
        coll(tc.collectionName).insert(tc.write(t))
    }

    def findOne[T](query: DBObject)(implicit tc: MongoDbModel[T], coll: String => MongoCollection): Option[T] = {
        coll(tc.collectionName).findOne(query).map {
            tc.read _
        }
    }

    def findById[T](id: ObjectId)(implicit tc: MongoDbModel[T], coll: String => MongoCollection): Option[T] = {
        coll(tc.collectionName).findOneByID(id).map {
            tc.read _
        }
    }

    def find[T](query: DBObject)(implicit tc: MongoDbModel[T], coll: String => MongoCollection): List[T] = {
        coll(tc.collectionName).find(query).map(tc.read _).toList
    }

    def find[T](query: DBObject, sort: DBObject, limit: Int)(implicit tc: MongoDbModel[T], coll: String => MongoCollection): List[T] = {
        coll(tc.collectionName).find(query).sort(sort).limit(limit).map {
            tc.read _
        }.toList
    }

    def delete[T](query: DBObject)(implicit tc: MongoDbModel[T], coll: String => MongoCollection): Unit = {
        coll(tc.collectionName).remove(query)
    }

    def count[T](implicit tc: MongoDbModel[T], coll: String => MongoCollection): Long =
        coll(tc.collectionName).count

    def count[T](query: DBObject)(implicit tc: MongoDbModel[T], coll: String => MongoCollection): Long =
        coll(tc.collectionName).count(query)
}