package core

import slick.driver.PostgresDriver
import slick.lifted.{CanBeQueryCondition, Rep, TableQuery}

import scala.concurrent.Future
import scala.reflect._
import PostgresDriver.api._
import slick.dbio.Effect
import slick.jdbc.PostgresProfile.api._
import slick.sql.{FixedSqlAction, FixedSqlStreamingAction}
import scala.concurrent.ExecutionContext.Implicits.global

import java.util.UUID

object DriverHelper {
  val user = "postgres"
  val url = "jdbc:postgresql://localhost:5432/mydatabase"
  val password = "wadefff"
  val jdbcDriver = "org.postgresql.Driver"
  val db = Database.forURL(url, user, password, driver = jdbcDriver)
}

trait BaseRepositoryComponent[T <: BaseTable[E], E <: BaseEntity] {
  def getById(uuid: String): Future[Option[E]]

  def getAll: Future[Seq[E]]

  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]]

  def save(row: E): Future[E]

  def deleteById(id: String): Future[Int]

  def updateById(id: String, row: E): Future[Int]
}

trait BaseRepositoryQuery[T <: BaseTable[E], E <: BaseEntity] {

  val query: PostgresDriver.api.type#TableQuery[T]

  def getByIdQuery(uuid: String) = {
    query.filter(x => x.uuid === uuid && x.isDeleted === false)
  }

  def insertTwoRowsQuery(row1: E, row2:E) = {
    (for {
      _ <- query returning query += row1
      r <- query returning query += row2
    } yield r).transactionally
  }

  def getAllQuery = {
    query.filter(_.isDeleted === false)
  }

  def filterQuery[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]) = {
    query.filter(expr).filter(_.isDeleted === false)
  }

  def insertAndThenFetchQuery(row: E) = {
    (for {
      _ <- query returning query += row
      insertedRecord <- query.filter(x => x.uuid === row.uuid && x.isDeleted === false).result
    } yield insertedRecord).transactionally
  }

  def saveQuery(row: E) = {
    query returning query += row
    //query.returning(query ) += row
  }

  def deleteByIdQuery(id: String) = {
    query.filter(_.uuid === id).map(_.isDeleted).update(true)
  }

  def updateByIdQuery(id: String, row: E) = {
    query.filter(_.uuid === id).update(row)
  }

}

abstract class BaseRepository[T <: BaseTable[E], E <: BaseEntity : ClassTag](clazz: TableQuery[T]) extends BaseRepositoryQuery[T, E] with BaseRepositoryComponent[T, E] {

  val clazzTable: TableQuery[T] = clazz
  lazy val clazzEntity = classTag[E].runtimeClass
  val query: PostgresDriver.api.type#TableQuery[T] = clazz
  val db: PostgresDriver.backend.DatabaseDef = DriverHelper.db

  override def getAll: Future[Seq[E]] = {
    db.run(getAllQuery.result)
  }

  def getById(uuid: String): Future[Option[E]] = {
    db.run(getByIdQuery(uuid).result.headOption)
  }

  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]) = {
    db.run(filterQuery(expr).result)
  }

  def save(row: E): Future[E] = {
    db.run(saveQuery(row))
  }

  def updateById(id: String, row: E) = {
    db.run(updateByIdQuery(id, row))
  }

  def deleteById(id: String) = {
    db.run(deleteByIdQuery(id))
  }

  def insertAndFetch(row: E): Future[Option[E]] = {
    db.run(insertAndThenFetchQuery(row)).map(_.headOption)
  }

  def insertTwoRows(r1: E, r2: E): Future[E] = {
    db.run(insertTwoRowsQuery(r1, r2)).recoverWith{
      case _ => throw new IllegalArgumentException("EXEXEXEXEXEX")
    }
  }

}


/*
abstract class WorkflowRepository[T <: WorkflowBaseTable[E], E <: WorkflowBaseEntity : ClassTag](clazz: TableQuery[T]) extends BaseRepository[T, E](clazz) {
  def approve(id:Long): Future[Int] = {
    db.run(getByIdQuery(id).map(_.isApproved).update(true))
  }
}*/
