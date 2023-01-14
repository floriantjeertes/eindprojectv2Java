package com.eindproject.v2.eindprojectv2.logic;

import com.eindproject.v2.eindprojectv2.Main;
import com.eindproject.v2.eindprojectv2.dal.DataBase;
import com.eindproject.v2.eindprojectv2.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ItemLogic {
    DataBase dataBase;
    public ItemLogic(DataBase dataBase){
        this.dataBase = dataBase;
    }
    public Item  LoanItem(int itemCode,User user)throws Exception{
        List<Item>  items  = GetItems();
        List<Item> update = new ArrayList<Item>();
        Item updateditem=null;
        for (Item item : items) {

            if(item.getItemCode()==itemCode){
                if(item.getLendstatus()==Lendstatus.Yes){
                    throw new Exception("item is already lent out");
                }
                item.setLender(user);
                item.setLendstatus(Lendstatus.Yes);
                item.setLendDate(LocalDateTime.now());
                updateditem =item;
                update.add(item);
                break;
            }
        }
        if(update.size()>1){
            throw new Exception("something went wrong your id has multiple hits.");
        }
        Main.dataBase.UpdateItem(update.get(0));

        if(GetItems(updateditem).size()==1){
            throw new Exception("book not updated correctly");
        }
        return updateditem;

    }
    public Item RecieveItem(int itemCode) throws Exception{
        List<Item>  items  = GetItems();

        List<Item> update = new ArrayList<Item>();
        Item updateditem=null;
        for (Item item : items) {
            if(item.getItemCode()==itemCode){
                if(item.getLendstatus()==Lendstatus.No){
                    throw new Exception("item is already handed in");
                }
                item.setLender(null);
                // item.setLendDate(LocalDateTime.MIN);
                item.setLendstatus(Lendstatus.No);
                updateditem =item;
                update.add(item);
                break;
            }
        }
        if(update.size()>1){
            throw new Exception("something went wrong your id has multiple hits.");
        }
        Main.dataBase.UpdateItem(update.get(0));

        return updateditem;
    }
    public List<Item> GetItems(){
        List<Item> items = Main.dataBase.GetItem();
        return items;

    }
    public List<Item> GetItems(Item filterItem){
        List<Item> items = Main.dataBase.GetItem(filterItem);

        return items;
    }
    public boolean CheckAvailableId(int id){
        List<Item> items = GetItems();
        for (Item item : items) {
            if(item.getItemCode() == id){
                return false;
            }
        }
        return true;
    }
    public int GetAmountOfItems(){
        List<Item> items = GetItems();
        return items.size();
    }

    public void DeleteItem(double ID) throws Exception{
        List<Item>  items  = GetItems();
        for (Item item : items) {
            if(item.getItemCode()==ID){

                Main.dataBase.DeleteItem(item);
                break;
            }
        }
    }
    public void AddItem(Item item) throws IOException{
        int id =  item.getItemCode();
        do {
            int itemamount =  GetAmountOfItems()+1;
            id = (int) (Math.random() * itemamount);

        } while (!CheckAvailableId(id));
        item.setItemCode(id);
        Main.dataBase.AddItem(item);
    }
    public void Update(Item item) throws IOException{
        Main.dataBase.UpdateItem(item);
    }


}