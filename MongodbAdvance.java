
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

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Aggregates.unwind;
/**eq method returns a Bson filter that tests if arg1 (logic) AND arg2**/
import static com.mongodb.client.model.Filters.and;
/**and method returns a Bson filter that tests if arg1 = arg2**/
import static com.mongodb.client.model.Filters.eq;
/**gt method returns a Bson filter that tests if arg1 > arg2**/
import static com.mongodb.client.model.Filters.gt;
/** a1 < a2 **/
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import java.util.Arrays;
/**Create Cursor Object to visit all documents in one query result**/
import com.mongodb.client.MongoCursor;

/** Create Index **/
import com.mongodb.client.model.Indexes;

/** project fields **/
import static com.mongodb.client.model.Projections.*;
/**
 * Use examples to show more MongoDB features including: query in array or
 * nested documents, complicated Bson filters, index, and Aggregate Pipelines.
 * 
 * @author jxie
 *
 */
public class MongodbAdvance {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/** connect to the database use the URI you got form Mongo **/
		String uri = "mongodb+srv://admin:admin@cluster0.fz8i8.mongodb.net/test";
		MongoClientURI clientURI = new MongoClientURI(uri);
		MongoClient client = new MongoClient(clientURI);
		MongoDatabase database = client.getDatabase("sample_restaurants");

		/** connect to the "restaurants" collection **/
		MongoCollection<Document> collection = database.getCollection("restaurants");

		/**
		 * Query in Array and nested documents. e.g. find the id of the first 5 Chinese cuisine
		 * restaurants with at least one score >15
		 **/
		// create a Bson filter eq AND gt
		Bson filter = and(eq("cuisine", "Chinese"), gt("grades.score", 15));
		MongoCursor<Document> cursor = collection.find(filter).projection(include("_id")).limit(5).iterator();
		try {
			while (cursor.hasNext()) {
				System.out.println(cursor.next().toJson());
			}
		} finally {
			cursor.close();
		}
		System.out.println("============================================================");

		/**
		 * More complicated Bson filter: eq AND (eq, OR(.., ..)) find first 5 Chinese cuisine
		 * restaurants with at least one score > 50 OR at least one grade = A
		 **/
		Bson filter2 = and(eq("cuisine", "Chinese"), or(gt("grades.score", 50), eq("grades.grade", "A")));
		MongoCursor<Document> cursor2 = collection.find(filter2).limit(5).iterator();
		try {
			while (cursor2.hasNext()) {
				System.out.println(cursor2.next().toJson());
			}
		} finally {
			cursor2.close();
		}
		System.out.println("============================================================");

		/**
		 * Use of aggregate pipeline. e.g Among American cuisine restaurants, group by
		 * borough and find the avg score for each borough, sort the result in dscending
		 * order, show the first 5
		 **/

		// The $match pipeline stage passes all documents matching the specified filter
		// to the next stage.
		//The data type of a pipeline stage is Bson
		Bson match = match(eq("cuisine", "American"));

		// The $unwind pipeline stage deconstructs an array field from the input
		// documents to output a document for each element.
		Bson unwind = unwind("$grades");

		// The $group pipeline stage groups documents by some specified expression and
		// outputs to the next stage a document for each distinct grouping.
		Bson group = group("$borough", avg("avgScore", "$grades.score"));

		// The sort stage
		Bson sort = sort(descending("avgScore"));

		// The limit stage
		Bson limit = limit(5);

//		MongoCursor<Document> cursor5 = collection.aggregate(Arrays.asList(match, unwind, group, sort, limit)).iterator();
//		try {
//			while (cursor5.hasNext()) {
//				System.out.println(cursor5.next().toJson());
//			}
//		} finally {
//			cursor5.close();
//		}
		System.out.println("============================================================");

		/** We use cuisine a lot. So create an index (ascending order) for cuisine. **/
		//single index
		collection.createIndex(Indexes.ascending("cuisine"));
		//compound: A more complicated example  
		collection.createIndex(Indexes.compoundIndex(Indexes.descending("borough"), Indexes.ascending("cuisine")));
		//drop index
		collection.dropIndex(Indexes.ascending("cuisine"));
		//use hint
		//Be careful: You must use an existing index
		MongoCursor<Document> cursor3 = collection.find(filter2).hint(Indexes.compoundIndex(Indexes.descending("borough"), Indexes.ascending("cuisine"))).limit(5).iterator();
		try {
			while (cursor3.hasNext()) {
				System.out.println(cursor3.next().toJson());
			}
		} finally {
			cursor2.close();
		}
		
		// close the client
		client.close();
	}

}
