package org.datavec.local.transforms;

import org.datavec.api.transform.ColumnOp;
import org.datavec.api.transform.DataAction;
import org.datavec.api.transform.Transform;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Writable;
import org.datavec.common.data.NDArrayWritable;
import org.datavec.dataframe.api.*;
import org.datavec.dataframe.columns.Column;
import org.datavec.dataframe.store.ColumnMetadata;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class for interop between
 * normal datavec records and
 * the dataframe
 *
 * @author Adam Gibson
 */
public class TableRecords {


    /**
     * Apply a transform process
     * to the given table
     * @param table the table to apply this to
     * @param transformProcess the transform
     *                         process to apply
     * @return the transformed table
     */
    public static Table transform(Table table,TransformProcess transformProcess) {
        List<DataAction> dataActions = transformProcess.getActionList();
        Table ret = table;
        for(DataAction dataAction : dataActions) {
            if(dataAction.getTransform() != null) {
                ret = transformTable(table,dataAction.getTransform(),true);
            }
            else if(dataAction.getFilter() != null) {

            }
            else if(dataAction.getCalculateSortedRank() != null) {

            }
            else if(dataAction.getConvertFromSequence() != null) {

            }
            else if(dataAction.getReducer() != null) {

            }
            else if(dataAction.getSequenceSplit() != null) {

            }
        }

        return ret;
    }

    /**
     * Run a transform operation on the table
     * @param table the table to run the transform operation on
     * @param transform the transform to run
     * @param inPlace whether the return results should be in place or a clone of the table
     * @return
     */
    public static Table transformTable(Table table,Transform transform,boolean inPlace) {
        if(!(transform instanceof ColumnOp)) {
            throw new IllegalArgumentException("Transform operation must be of type ColumnOp");
        }

        Table ret = inPlace ? table : table.emptyCopy();

        String[] columnNames = transform.columnNames();
        String[] newColumnNames = transform.outputColumnNames();

        for(String columnName : columnNames) {
            Column column = table.column(columnName);
            Column retColumn = ret.column(columnName);
            if(column instanceof FloatColumn) {
                FloatColumn floatColumn = (FloatColumn) column;
                FloatColumn retFloatColumn = (FloatColumn) retColumn;
                if(newColumnNames.length == 1)
                    for(int i = 0; i < floatColumn.size(); i++) {
                        retFloatColumn.set(i, (Float) transform.map(floatColumn.get(i)));
                    }
                else {
                    //remove the column and append new columns on to the end.
                    //map is going to produce more than 1 output it will be easier to add it to the end
                    ret.removeColumn(ret.columnIndex(retColumn));

                    for(int i = 0; i < floatColumn.size(); i++) {

                    }

                }

            }
            else if(column instanceof LongColumn) {
                LongColumn longColumn = (LongColumn) column;
                LongColumn retLongColumn = (LongColumn) retColumn;
                if(newColumnNames.length == 1)
                    for(int i = 0; i < longColumn.size(); i++) {
                        retLongColumn.set(i, (Long) transform.map(longColumn.get(i)));
                    }
                else {
                    //remove the column and append new columns on to the end.
                    //map is going to produce more than 1 output it will be easier to add it to the end
                    ret.removeColumn(ret.columnIndex(retColumn));
                }
            }
            else if(column instanceof BooleanColumn) {
                BooleanColumn booleanColumn = (BooleanColumn) column;
                BooleanColumn retBooleanColumn = (BooleanColumn) retColumn;
                if(newColumnNames.length == 1)
                    for(int i = 0; i < booleanColumn.size(); i++) {
                        retBooleanColumn.set(i, (Boolean) transform.map(booleanColumn.get(i)));
                    }
                else {
                    //remove the column and append new columns on to the end.
                    //map is going to produce more than 1 output it will be easier to add it to the end
                    ret.removeColumn(ret.columnIndex(retColumn));
                }
            }
            else if(column instanceof CategoryColumn) {
                CategoryColumn categoryColumn = (CategoryColumn) column;
                CategoryColumn retCategoryColumn = (CategoryColumn) retColumn;
                if(newColumnNames.length == 1)
                    for(int i = 0; i < categoryColumn.size(); i++) {
                        retCategoryColumn.set(i, (String) transform.map(categoryColumn.get(i)));
                    }
                else {
                    //remove the column and append new columns on to the end.
                    //map is going to produce more than 1 output it will be easier to add it to the end
                    ret.removeColumn(ret.columnIndex(retColumn));
                }
            }
            else if(column instanceof DateColumn) {
                DateColumn dateColumn = (DateColumn) column;
                DateColumn retDateColumn = (DateColumn) retColumn;
                if(newColumnNames.length == 1)
                    for(int i = 0; i < dateColumn.size(); i++) {
                        retDateColumn.set(i, (Integer) transform.map(dateColumn.get(i)));
                    }
                else {
                    //remove the column and append new columns on to the end.
                    //map is going to produce more than 1 output it will be easier to add it to the end
                    ret.removeColumn(ret.columnIndex(retColumn));
                }
            }

            else if(column instanceof IntColumn) {
                IntColumn intColumn = (IntColumn) column;
                IntColumn retIntColumn = (IntColumn) retColumn;
                if(newColumnNames.length == 1)
                    for(int i = 0; i < intColumn.size(); i++) {
                        retIntColumn.set(i, (Integer) transform.map(intColumn.get(i)));
                    }
                else {
                    //remove the column and append new columns on to the end.
                    //map is going to produce more than 1 output it will be easier to add it to the end
                    ret.removeColumn(ret.columnIndex(retColumn));
                }
            }
            else if(column instanceof ShortColumn) {
                ShortColumn shortColumn = (ShortColumn) column;
                ShortColumn retShortColumn = (ShortColumn) retColumn;
                if(newColumnNames.length == 1)
                    for(int i = 0; i < shortColumn.size(); i++) {
                        retShortColumn.set(i, (Short) transform.map(shortColumn.get(i)));
                    }
                else {
                    //remove the column and append new columns on to the end.
                    //map is going to produce more than 1 output it will be easier to add it to the end
                    ret.removeColumn(ret.columnIndex(retColumn));

                }
            }


            else {
                throw new IllegalStateException("Illegal column type " + column.getClass());
            }

            if(columnNames.length == 1) {
                column.setName(newColumnNames[0]);
            }

        }

        return table;
    }

    /**
     * Create a matrix from a table where
     * the matrix will be of n rows x m columns
     *
     * @param table the table to create
     * @return the matrix created from this table
     */
    public static INDArray arrayFromTable(Table table) {
        INDArray arr = Nd4j.create(table.rowCount(),table.columnCount());
        for(int i = 0; i < table.rowCount(); i++) {
            for(int j = 0; j < table.columnCount(); j++) {
                arr.putScalar(i,j,Double.valueOf(table.get(j,i)));
            }
        }

        return arr;
    }

    /**
     * Convert an all numeric table
     * to a list of records
     * @param table the table to convert
     * @return the list of records from
     * the given table
     */
    public static List<List<Writable>> fromTable(Table table) {
        List<List<Writable>> ret = new ArrayList<>();
        for(int i = 0; i < table.rowCount(); i++) {
            ret.add(new ArrayList<Writable>());
            for(int j = 0; j < table.columnCount(); j++) {
                ret.get(i).add(new DoubleWritable(Double.valueOf(table.get(j,i))));
            }
        }
        return ret;
    }

    /**
     * Create a table from records and
     * a given schema.
     * The records should either be writables such that
     * each list should be interpreted as a row in the table.
     * Optionally, you can also have singleton lists of
     * {@link NDArrayWritable} to represent columns.
     * The given ndarray must be a row of 1 x m where m
     * is schema.numColumns()
     * @param writable the records to create the table from
     * @param schema the schema to use
     * @return the created table
     */
    public static Table fromRecordsAndSchema(List<List<Writable>> writable, Schema schema) {
        Table table = Table.create("table",columnsForSchema(schema));
        for(int i = 0; i < writable.size(); i++) {
            List<Writable> row = writable.get(i);
            if(row.size() == 1 && row.get(0) instanceof NDArrayWritable) {
                NDArrayWritable ndArrayWritable = (NDArrayWritable) row.get(0);
                INDArray arr = ndArrayWritable.get();
                if(arr.columns() != schema.numColumns())
                    throw new IllegalArgumentException("Found ndarray writable of illegal size " + arr.columns());
                for(int j = 0; j < arr.length(); j++) {
                    table.floatColumn(j).add(arr.getDouble(j));
                }
            }
            else if(row.size() == schema.numColumns()) {
                for(int j = 0; j < row.size(); j++) {
                    table.floatColumn(j).add(row.get(j).toDouble());
                }
            }
            else
                throw new IllegalArgumentException("Illegal writable list of size " + row.size() + " at index " + i);
        }
        return table;
    }

    /**
     * Extract a column array from the given schema
     * @param schema the schema to get columns from
     * @return a column array based on the given schema
     */
    public static Column[] columnsForSchema(Schema schema) {
        Column[] ret = new Column[schema.numColumns()];
        for(int i = 0; i < schema.numColumns(); i++) {
            switch(schema.getType(i)) {
                case Double: ret[i] = new FloatColumn(schema.getName(i)); break;
                case Float: ret[i] = new FloatColumn(schema.getName(i)); break;
                case Long: ret[i] = new LongColumn(schema.getName(i)); break;
                case Integer: ret[i] = new IntColumn(schema.getName(i)); break;
                case Categorical: ret[i] = new CategoryColumn(schema.getName(i),4); break;
                case Time: ret[i] = new DateColumn(new ColumnMetadata(new LongColumn(schema.getName(i)))); break;
                case Boolean: ret[i] = new BooleanColumn(new ColumnMetadata(new IntColumn(schema.getName(i)))); break;
            }
        }
        return ret;
    }



}
