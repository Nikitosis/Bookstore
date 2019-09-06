package com.services;

import com.dao.ClientDao;
import com.models.Book;
import com.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private ClientDao clientDao;

    @Autowired
    public ClientService(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    public List<Client> findAll(){
        return clientDao.findAll();
    }

    public Client findById(Long id){
        return clientDao.findById(id);
    }

    public Long save(Client book){
        return clientDao.save(book);
    }

    public void update(Client book){
        clientDao.update(book);
    }

    public void delete(Long id){
        clientDao.delete(id);
    }
}
