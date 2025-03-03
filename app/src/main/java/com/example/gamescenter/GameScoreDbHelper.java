package com.example.gamescenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameScoreDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "GameScoreDbHelper";

    // Database Info
    private static final String DATABASE_NAME = "GameScoresDatabase";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_SCORES = "scores";

    // Score Table Columns
    private static final String KEY_SCORE_ID = "id";
    private static final String KEY_PLAYER_NAME = "player_name";
    private static final String KEY_GAME_NAME = "game_name";
    private static final String KEY_SCORE_VALUE = "score";
    private static final String KEY_SCORE_DATE = "date";

    // Date format for displaying timestamps
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    // Add "All" options for filters
    private static final String ALL_OPTION = "All";

    private static volatile GameScoreDbHelper instance;

    public static synchronized GameScoreDbHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (GameScoreDbHelper.class) {
                if (instance == null) {
                    instance = new GameScoreDbHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    GameScoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCORES_TABLE = "CREATE TABLE " + TABLE_SCORES +
                " (" +
                KEY_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_PLAYER_NAME + " TEXT NOT NULL, " +
                KEY_GAME_NAME + " TEXT NOT NULL, " +
                KEY_SCORE_VALUE + " INTEGER NOT NULL, " +
                KEY_SCORE_DATE + " INTEGER NOT NULL" +  // Store as milliseconds since epoch for consistency
                ")";
        db.execSQL(CREATE_SCORES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        // Proper migration strategy (instead of dropping table)
        if (oldVersion < 2) {
            // If using DATETIME in older version, convert to INTEGER (milliseconds)
            try {
                // Try to convert existing dates to milliseconds if needed
                db.execSQL("ALTER TABLE " + TABLE_SCORES + " RENAME TO temp_" + TABLE_SCORES);
                onCreate(db); // Create new table with correct schema

                // Copy data, converting date if needed
                db.execSQL("INSERT INTO " + TABLE_SCORES +
                        " SELECT " + KEY_SCORE_ID + ", " +
                        KEY_PLAYER_NAME + ", " +
                        KEY_GAME_NAME + ", " +
                        KEY_SCORE_VALUE + ", " +
                        "strftime('%s', " + KEY_SCORE_DATE + ") * 1000 " +  // Convert to milliseconds
                        " FROM temp_" + TABLE_SCORES);

                db.execSQL("DROP TABLE temp_" + TABLE_SCORES);
                Log.d(TAG, "Successfully migrated date format in version 2");
            } catch (Exception e) {
                Log.e(TAG, "Error during migration: " + e.getMessage());
            }
        }

        // Add future version migrations here
    }

    public List<ScoreEntry> getAllScores() {
        List<ScoreEntry> scoresList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        if (db == null) {
            Log.e(TAG, "Database is null!");
            return scoresList;
        }

        String QUERY = "SELECT * FROM " + TABLE_SCORES +
                " ORDER BY " + KEY_SCORE_VALUE + " DESC";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(QUERY, null);

            if (cursor.moveToFirst()) {
                do {
                    ScoreEntry entry = new ScoreEntry();
                    entry.id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE_ID));
                    entry.playerName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLAYER_NAME));
                    entry.gameName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_GAME_NAME));
                    entry.score = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE_VALUE));

                    // Convert timestamp to formatted date string
                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_SCORE_DATE));
                    entry.date = formatDate(timestamp);

                    scoresList.add(entry);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting all scores: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return scoresList;
    }

    // Helper method to format timestamp to readable date
    private String formatDate(long timestamp) {
        try {
            return DATE_FORMAT.format(new Date(timestamp));
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date: " + e.getMessage());
            return "Unknown date";
        }
    }

    public List<ScoreEntry> getScoresByGame(String gameName) {
        // Return all scores if "All" is selected
        if (gameName == null || gameName.isEmpty() || gameName.equals(ALL_OPTION)) {
            return getAllScores();
        }

        List<ScoreEntry> scoresList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        if (db == null) {
            Log.e(TAG, "Database is null!");
            return scoresList;
        }

        String QUERY = "SELECT * FROM " + TABLE_SCORES +
                " WHERE " + KEY_GAME_NAME + " = ?" +
                " ORDER BY " + KEY_SCORE_VALUE + " DESC";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(QUERY, new String[]{gameName});

            if (cursor.moveToFirst()) {
                do {
                    ScoreEntry entry = new ScoreEntry();
                    entry.id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE_ID));
                    entry.playerName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLAYER_NAME));
                    entry.gameName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_GAME_NAME));
                    entry.score = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE_VALUE));

                    // Convert timestamp to formatted date string
                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_SCORE_DATE));
                    entry.date = formatDate(timestamp);

                    scoresList.add(entry);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting scores by game: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return scoresList;
    }

    public List<ScoreEntry> getScoresByPlayer(String playerName) {
        // Return all scores if "All" is selected
        if (playerName == null || playerName.isEmpty() || playerName.equals(ALL_OPTION)) {
            return getAllScores();
        }

        List<ScoreEntry> scoresList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        if (db == null) {
            Log.e(TAG, "Database is null!");
            return scoresList;
        }

        String QUERY = "SELECT * FROM " + TABLE_SCORES +
                " WHERE " + KEY_PLAYER_NAME + " = ?" +
                " ORDER BY " + KEY_SCORE_VALUE + " DESC";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(QUERY, new String[]{playerName});

            if (cursor.moveToFirst()) {
                do {
                    ScoreEntry entry = new ScoreEntry();
                    entry.id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE_ID));
                    entry.playerName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLAYER_NAME));
                    entry.gameName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_GAME_NAME));
                    entry.score = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE_VALUE));

                    // Convert timestamp to formatted date string
                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_SCORE_DATE));
                    entry.date = formatDate(timestamp);

                    scoresList.add(entry);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting scores by player: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return scoresList;
    }

    public List<ScoreEntry> getFilteredScores(String gameName, String playerName) {
        // If both filters are "All"
        if ((gameName == null || gameName.isEmpty() || gameName.equals(ALL_OPTION)) &&
                (playerName == null || playerName.isEmpty() || playerName.equals(ALL_OPTION))) {
            return getAllScores();
        }

        // If only game filter is active
        if (playerName == null || playerName.isEmpty() || playerName.equals(ALL_OPTION)) {
            return getScoresByGame(gameName);
        }

        // If only player filter is active
        if (gameName == null || gameName.isEmpty() || gameName.equals(ALL_OPTION)) {
            return getScoresByPlayer(playerName);
        }

        // Both filters are active
        List<ScoreEntry> scoresList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        if (db == null) {
            Log.e(TAG, "Database is null!");
            return scoresList;
        }

        String QUERY = "SELECT * FROM " + TABLE_SCORES +
                " WHERE " + KEY_GAME_NAME + " = ? AND " + KEY_PLAYER_NAME + " = ?" +
                " ORDER BY " + KEY_SCORE_VALUE + " DESC";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(QUERY, new String[]{gameName, playerName});

            if (cursor.moveToFirst()) {
                do {
                    ScoreEntry entry = new ScoreEntry();
                    entry.id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE_ID));
                    entry.playerName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLAYER_NAME));
                    entry.gameName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_GAME_NAME));
                    entry.score = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE_VALUE));

                    // Convert timestamp to formatted date string
                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_SCORE_DATE));
                    entry.date = formatDate(timestamp);

                    scoresList.add(entry);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting filtered scores: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return scoresList;
    }

    public void displayScores(Context context, ListView listView, Spinner gameFilter, Spinner playerFilter) {
        // Check for null UI elements
        if (context == null || listView == null || gameFilter == null || playerFilter == null) {
            Log.e(TAG, "One or more UI elements are null!");
            return;
        }

        // Populate filters first
        populateFilters(context, gameFilter, playerFilter);

        // Then display all scores
        updateScoresList(context, listView, null, null);
    }

    public void updateScoresList(Context context, ListView listView, String gameFilter, String playerFilter) {
        if (context == null || listView == null) {
            Log.e(TAG, "Context or ListView is null!");
            return;
        }

        List<ScoreEntry> filteredScores = getFilteredScores(gameFilter, playerFilter);
        List<String> scoresList = new ArrayList<>();

        for (ScoreEntry entry : filteredScores) {
            String scoreText = entry.playerName + " - " + entry.gameName + " - " + entry.score + " - " + entry.date;
            scoresList.add(scoreText);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, scoresList);
        listView.setAdapter(adapter);
    }

    private void populateFilters(Context context, Spinner gameFilter, Spinner playerFilter) {
        if (context == null || gameFilter == null || playerFilter == null) {
            Log.e(TAG, "Context or Spinners are null!");
            return;
        }

        SQLiteDatabase db = getReadableDatabase();
        List<String> games = new ArrayList<>();
        List<String> players = new ArrayList<>();

        // Add "All" option as first choice
        games.add(ALL_OPTION);
        players.add(ALL_OPTION);

        if (db == null) {
            Log.e(TAG, "Database is null!");
            // Still set adapters with just the "All" option
            setFilterAdapters(context, gameFilter, playerFilter, games, players);
            return;
        }

        Cursor gameCursor = null;
        Cursor playerCursor = null;

        try {
            gameCursor = db.rawQuery("SELECT DISTINCT " + KEY_GAME_NAME + " FROM " + TABLE_SCORES + " ORDER BY " + KEY_GAME_NAME, null);
            playerCursor = db.rawQuery("SELECT DISTINCT " + KEY_PLAYER_NAME + " FROM " + TABLE_SCORES + " ORDER BY " + KEY_PLAYER_NAME, null);

            if (gameCursor.moveToFirst()) {
                do {
                    games.add(gameCursor.getString(0));
                } while (gameCursor.moveToNext());
            }

            if (playerCursor.moveToFirst()) {
                do {
                    players.add(playerCursor.getString(0));
                } while (playerCursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating filters: " + e.getMessage());
        } finally {
            if (gameCursor != null) {
                gameCursor.close();
            }
            if (playerCursor != null) {
                playerCursor.close();
            }
        }

        setFilterAdapters(context, gameFilter, playerFilter, games, players);
    }

    private void setFilterAdapters(Context context, Spinner gameFilter, Spinner playerFilter,
                                   List<String> games, List<String> players) {
        ArrayAdapter<String> gameAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, games);
        gameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameFilter.setAdapter(gameAdapter);

        ArrayAdapter<String> playerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, players);
        playerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerFilter.setAdapter(playerAdapter);
    }

    public static class ScoreEntry {
        public int id;
        public String playerName;
        public String gameName;
        public int score;
        public String date;

        @Override
        public String toString() {
            return playerName + " - " + gameName + " - " + score + " - " + date;
        }
    }

    public void insertScore(String playerName, String gameName, int score) {
        SQLiteDatabase db = getWritableDatabase();

        if (db == null) {
            Log.e(TAG, "Database is null!");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYER_NAME, playerName);
        values.put(KEY_GAME_NAME, gameName);
        values.put(KEY_SCORE_VALUE, score);
        values.put(KEY_SCORE_DATE, System.currentTimeMillis()); // Store current time in milliseconds

        long newRowId = db.insert(TABLE_SCORES, null, values);
        if (newRowId == -1) {
            Log.e(TAG, "Error inserting score");
        } else {
            Log.d(TAG, "Score inserted successfully, ID: " + newRowId);
        }
    }

    public List<ScoreEntry> getFilteredScores(String gameName, String playerName, boolean sortByHighestScore) {
        String orderBy = sortByHighestScore ? KEY_SCORE_VALUE + " DESC" : KEY_SCORE_DATE + " DESC";

        List<ScoreEntry> scoresList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        if (db == null) {
            Log.e(TAG, "Database is null!");
            return scoresList;
        }

        String QUERY = "SELECT * FROM " + TABLE_SCORES + " WHERE 1=1";
        List<String> selectionArgs = new ArrayList<>();

        if (gameName != null && !gameName.equals(ALL_OPTION)) {
            QUERY += " AND " + KEY_GAME_NAME + " = ?";
            selectionArgs.add(gameName);
        }

        if (playerName != null && !playerName.equals(ALL_OPTION)) {
            QUERY += " AND " + KEY_PLAYER_NAME + " = ?";
            selectionArgs.add(playerName);
        }

        QUERY += " ORDER BY " + orderBy;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(QUERY, selectionArgs.toArray(new String[0]));
            if (cursor.moveToFirst()) {
                do {
                    ScoreEntry entry = new ScoreEntry();
                    entry.id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE_ID));
                    entry.playerName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLAYER_NAME));
                    entry.gameName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_GAME_NAME));
                    entry.score = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCORE_VALUE));

                    // Convert timestamp to formatted date string
                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_SCORE_DATE));
                    entry.date = formatDate(timestamp);

                    scoresList.add(entry);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting filtered scores: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return scoresList;
    }

    public void updateScoresList(Context context, ListView listView, String gameFilter, String playerFilter, boolean sortByHighestScore) {
        if (context == null || listView == null) {
            Log.e(TAG, "Context or ListView is null!");
            return;
        }

        List<ScoreEntry> filteredScores = getFilteredScores(gameFilter, playerFilter, sortByHighestScore);
        List<String> scoresList = new ArrayList<>();

        for (ScoreEntry entry : filteredScores) {
            String scoreText = "Name: " + entry.playerName + " | Game: " + entry.gameName +
                    " | Score: " + entry.score + " | Time: " + entry.date;
            scoresList.add(scoreText);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, scoresList);
        listView.setAdapter(adapter);
    }
}