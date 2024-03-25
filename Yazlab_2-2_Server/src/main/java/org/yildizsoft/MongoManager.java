package org.yildizsoft;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.util.List;

public class MongoManager
{
    private static final String mongoUri = "mongodb://localhost:27017";
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;

    public static void Start() throws MongoException
    {
        mongoClient = MongoClients.create(mongoUri);
        mongoDatabase = mongoClient.getDatabase("yazlab_2-2");
    }

    public static void Stop()
    {
        mongoClient.close();
    }

    public static void AddEntry(String collection, List<String[]> entry)
    {
        try
        {
            MongoCollection<Document> col = mongoDatabase.getCollection(collection);
            Document doc = new Document();
            doc.append("_id", new ObjectId());

            for (String[] contents : entry)
            {
                doc.append(contents[0], contents[1]);
            }

            col.insertOne(doc);
        }
        catch(MongoException e)
        {
            System.err.println('"' + entry.toString() + "\" veritabanına kaydedilemedi.\n" + e);
        }
    }

    public static int AddUser(List<String> entry)
    {
        try
        {
            MongoCollection<Document> col = mongoDatabase.getCollection("users");
            Document document = new Document();
            document.append("_id", new ObjectId());
            document.append("username", entry.getFirst());
            document.append("password", entry.get(1));

            FindIterable<Document> users = col.find(Filters.exists("username"));
            for(Document d : users)
            {
                if(d.getString("username").equals(document.getString("username")))
                {
                    System.err.println("Kullanıcı \"" + document.toJson() + "\" zaten veritabanında kayıtlı, ekleme yapılmıyor.");
                    return 1;
                }
            }

            col.insertOne(document);
            System.out.println("Kullanıcı başarıyla eklendi: " + document.toJson());
            return 0;
        }
        catch(MongoException e)
        {
            System.err.println('"' + entry.toString() + "\" veritabanına kaydedilemedi.\n" + e);
            return -1;
        }
    }

    public static boolean UserExists(List<String> entry)
    {
        try
        {
            MongoCollection<Document> col = mongoDatabase.getCollection("users");
            FindIterable<Document> users = col.find(Filters.exists("username"));

            for(Document user : users)
            {
                if(user.getString("username").equals(entry.getFirst()) && user.getString("password").equals(entry.get(1)))
                    return true;
            }
            return false;
        }
        catch(MongoException e)
        {
            System.err.println("Veritabanı aranırken bir hata oluştu.\n\n" + e);
            return false;
        }
    }
}
