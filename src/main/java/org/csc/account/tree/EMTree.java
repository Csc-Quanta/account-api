package org.csc.account.tree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Data
@Slf4j
public class EMTree {

    ETNode root = new ETNode("root", null);

    IETStorage storage;

    @Override
    public String toString() {
        return "EMTree@" + ",root=" + toJsonString();
    }

    public ETNode delete(byte[]keybb) {
        return root.delete(EHelper.mapByteToString(keybb), storage);
    }

    public ETNode delete(String keymap) {
        return root.delete(keymap, storage);
    }

    public byte[] encode() {
        byte[]rootbb = root.encode(storage);
        root.flushMemory(0);
        return rootbb;
    }

    public String toJsonString() {
        return root.toJsonString();
    }

    public ETNode findByKey(byte[]keybb) {

        return root.getByKey(EHelper.mapByteToString(keybb), storage);
    }

    public ETNode findByKey(String keystr) {
        return root.getByKey(keystr, storage);
    }

    public ETNode insert(ETNode node, String key, byte[] value, int deep) {
        node.setDirty(true);
        if (deep > key.length() - 1) {
            ETNode leafNode = new ETNode(key, value);
            leafNode.setLeafNode(true);
            node.appendLeafNode(leafNode);
        } else {
            char ch = key.charAt(deep);
            ETNode child = node.getChild(ch, storage);
            if (child == null) {
                node.appendChildNode(new ETNode(key, value), ch);
            } else if (key.equals(child.getKey())) {
				/*log.debug("overrided child:@" + ch + ",key=" + node.getKey()+",hex="+EHelper.encAPI.hexEnc(EHelper.mapStringToByte(node.getKey())));
                if (!"root".equals(key)) {
					log.debug("tree.before.key::"+key+"::"+root.toJsonString());
                }*/
                node.overrideChildNode(ch, value);
                /*if (!"root".equals(key)) {
					log.debug("tree.after.key::"+key+"::"+root.toJsonString());
                }*/
            } else {
                insert(child, key, value, deep + 1);
            }
        }
        return node;
    }

    public void put(byte[] key, byte[] value) {
        putM(EHelper.bytesToMapping(key), value);
    }

    public void put(String hexEnc, byte[] value) {
        putM(EHelper.bytesToMapping(hexEnc), value);
    }

    public void putM(String mapKey, byte[] value) {
        if (root == null) {
            root = new ETNode(mapKey, value);
        } else {
            if (value == null || value.length == 0) {
                root = delete(mapKey);
            } else {
                root = insert(root, mapKey, value, 0);
            }
        }
    }

    public EMTree(IETStorage storage) {
        super();
        this.storage = storage;
    }

}
