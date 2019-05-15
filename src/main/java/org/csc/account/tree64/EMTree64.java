package org.csc.account.tree64;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@AllArgsConstructor
@Data
@Slf4j
public class EMTree64 {

    ETNode64 root = new ETNode64(null);

    IETStorage64 storage;

    @Override
    public String toString() {
        return "EMTree@" + ",root=" + toJsonString();
    }

    public ETNode64 delete(byte[] key) {
        return root.delete(key, storage);
    }

    public byte[] encode(int blocknumber) {
        byte[] rootbb = root.eencode(storage,blocknumber);
//        root.flushMemory(0);
        return rootbb;
    }

    public ETNode64.Stat getStat(int blocknumber) {
        if (root != null) {
            root.eencode(storage,blocknumber);
            return root.getStat();
        } else {
            return new ETNode64.Stat();
        }
    }

    public String toJsonString() {
        return root.toJsonString();
    }

    public ETNode64 findByKey(byte[] key) {
        return root.getByKey(key, storage);
    }

    public ETNode64 insert(ETNode64 node, byte[] key, byte[] value, int deep) {
        node.setDirty(true);
        int keySize = ETNode64.keySize(key);
        if (deep > keySize - 1) {
            ETNode64 leafNode = new ETNode64(key, value);
            leafNode.setLeafNode(true);
            node.appendLeafNode(leafNode);
        } else {
            int idx = ETNode64.keyIndexAt(key, deep);
            ETNode64 child = node.getChild(idx, storage);
            if (child == null) {
                node.appendChildNode(new ETNode64(key, value), idx);

            } else if (Arrays.equals(key, child.getKey())) {
                node.overrideChildNode(idx, value);
            } else {
                insert(child, key, value, deep + 1);
            }
        }
        return node;
    }

    public void put(byte[] key, byte[] value) {
        if (root == null) {
            root = new ETNode64(key, value);
        } else {
            if (value == null || value.length == 0) {
                root = delete(key);
            } else {
                root = insert(root, key, value, 0);
            }
        }
    }

    public EMTree64(IETStorage64 storage) {
        this.storage = storage;
    }

}
