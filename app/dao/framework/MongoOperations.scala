package dao.framework

import com.mongodb.casbah.Imports._
import MongoConnectionProperties._
import dao.framework.MongoConnectionPool

trait MongoDbModel[T] {
    def collectionName: String

    def write(t: T): DBObject

    def read(dbo: DBObject): T
}

trait MongoOperations extends MongoConnectionPool {
    def save[T](t: T)(implicit tc: MongoDbModel[T], coll: MongoCollectionName => MongoCollection): Unit =
        coll(tc.collectionName).insert(tc.write(t))

    def findOne[T](query: DBObject)(implicit tc: MongoDbModel[T], coll: MongoCollectionName => MongoCollection): Option[T] =
        coll(tc.collectionName).findOne(query).map( dbo => tc.read(dbo) )

    def findById[T](id: ObjectId)(implicit tc: MongoDbModel[T], coll: MongoCollectionName => MongoCollection): Option[T] =
        coll(tc.collectionName).findOneByID(id).map( dbo => tc.read(dbo) )

    def find[T](query: DBObject)(implicit tc: MongoDbModel[T], coll: MongoCollectionName => MongoCollection): List[T] =
        coll(tc.collectionName).find(query).map( dbo => tc.read(dbo) ).toList

    def find[T](query: DBObject, sort: DBObject, limit: Int)(implicit tc: MongoDbModel[T], coll: MongoCollectionName => MongoCollection): List[T] =
        coll(tc.collectionName).find(query).sort(sort).limit(limit).map( dbo => tc.read(dbo) ).toList

    def delete[T](query: DBObject)(implicit tc: MongoDbModel[T], coll: MongoCollectionName => MongoCollection): Unit =
        coll(tc.collectionName).remove(query)

    def count[T](implicit tc: MongoDbModel[T], coll: MongoCollectionName => MongoCollection): Long =
        coll(tc.collectionName).count()

    def count[T](query: DBObject)(implicit tc: MongoDbModel[T], coll: MongoCollectionName => MongoCollection): Long =
        coll(tc.collectionName).count(query)
}