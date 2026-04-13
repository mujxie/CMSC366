
/**Connect to database**/
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
/**Connect to a collection**/
import com.mongodb.client.MongoCollection;
/**Create Document Objects**/
import org.bson.Document;
/**Create Bson Objects (e.g. Bson filters)**/
import org.bson.conversions.Bson;
/**delete() and deleteOne() methods return DeleteResult Object**/
import com.mongodb.client.result.DeleteResult;
/**eq method returns a Bson filter that tests if arg1 = arg2**/
import static com.mongodb.client.model.Filters.eq;
/**Create BasicDBObject**/
import com.mongodb.BasicDBObject;
/**Create Cursor Object to visit all documents in one query result**/
import com.mongodb.client.MongoCursor;


/** 
 * Use examples to show basic CRUD operations of MongoDB
 * @author jxie
 *
 */

public class MongodbBasics {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/** connect to the database use the URI you got form Mongo **/
		String uri = "mongodb+srv://admin:admin@cluster0.fz8i8.mongodb.net/test";
		MongoClientURI clientURI = new MongoClientURI(uri);
		MongoClient client = new MongoClient(clientURI);
		MongoDatabase database = client.getDatabase("sample_restaurants");

		/** Connect to or Create a collection "test" **/
		MongoCollection<Document> coll = database.getCollection("test_new4");

		/** Create a new document and insert it into the collection we connected **/
//		Document docu = new Document("name", "Xie");
//		docu.append("Sex", "Male");
//		docu.append("Age", "21");
//		docu.append("Race", "Oriental");
//		coll.insertOne(docu);

		/** Delete a document from the collection using Bson filter **/
		//Bson filter: name = Xie
		Bson deleteKey = eq("name", "Xie");
		// Delete ONE document with name = Xie
		DeleteResult result = coll.deleteOne(deleteKey);
		System.out.println(result);
		System.out.println("============================================================");

		/**
		 * Query Using Document Objects e.g. Find the first American cuisine restaurant
		 * in Bronx
		 **/
		// connect to the "restaurants" collection
		MongoCollection<Document> collection = database.getCollection("restaurants");
		// create a Document Object with two name/value pairs (cuisine: American,
		// borough: Bronx)
		Document doc = new Document("cuisine", "American");
		doc.append("borough", "Bronx");
		// A Document object can be the parameter of find method. first() returns the
		// first document (record)
		Document found = (Document) collection.find(doc).first();
		if (found != null) {
			System.out.println(found.toJson());
		}
		System.out.println("============================================================");

		/**
		 * Query Using BasicDBObject and Regex e.g. list all Chinese cuisine restaurants
		 * whose names start with M
		 **/
		BasicDBObject regexQuery = new BasicDBObject();
		// a regular expression: String starts with M
		regexQuery.put("name", new BasicDBObject("$regex", "^M.*"));
		// You can append more pairs just like Document Object
		regexQuery.append("cuisine", "Chinese");
		// iterator() returns a MongoCursor Object
		MongoCursor<Document> cursor = collection.find(regexQuery).iterator();
		try {
			while (cursor.hasNext()) {
				System.out.println(cursor.next().toJson());
			}
		} finally {
			cursor.close();
		}
		System.out.println("============================================================");

		/**
		 * update the first Chinese cuisine restaurant, change its name to May May
		 * Kitchen Kai
		 **/
		// Use Document to find the first Chinese cuisine restaurant. Don't forget
		// first() method.
		Document doc2 = new Document("cuisine", "Chinese");
		Document found2 = (Document) collection.find(doc2).first();
		System.out.println("***Before Update******" + found2.toJson());
		// Use Bson Object to update existing pairs with operator: $set and
		// updateOne/update method.
		Bson updateValue = new Document("name", "May May Kitchen");
		Bson update = new Document("$set", updateValue);
		collection.updateOne(found2, update);
		System.out.println("***After Update******" + found2.toJson()); // What is the name of the restaurant?
		System.out.println("============================================================");

		/** The use of limit() method. e.g. find the first 10 Chinese cuisine restaurants **/
		MongoCursor<Document> cursor2 = collection.find(doc2).limit(10).iterator();
		try {
			while (cursor2.hasNext()) {
				System.out.println(cursor2.next().toJson());
			}
		} finally {
			cursor2.close();
		}
		System.out.println("============================================================");

		// Close the client
		client.close();
	}

}
